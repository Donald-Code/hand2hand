package tshabalala.bongani.courierservice;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.helper.LocationDB;
import tshabalala.bongani.courierservice.model.Locations;
import tshabalala.bongani.courierservice.services.LocationService;

class BackgroundTask {

    private Context context;
    private static Integer counter = 0;
    public static Long last_run = 0L;

    void setContext(Context c) {
        context = c;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null) {
                Log.d("TAG", "NETWORK CONNECTION: " + networkInfo.getSubtypeName());
                return networkInfo.isConnected();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    void run() {

        // Prevent the task running too often
        // TODO: Figure out why it is being triggered so often
        if((last_run - 60000 + Common.TRACKING_INTERVAL * 60000) <= System.currentTimeMillis()) {
            Common.NETWORK_ON = this.isNetworkAvailable();
            BackgroundTask.last_run = System.currentTimeMillis();

            Log.d("TAG","Background service running!");
            counter++;

            Log.d("TAG", counter + " Hello from: " + Common.GPS_LAT + " " + Common.GPS_LONG + " last updated: " + LocationService.updated);
            if (Common.NETWORK_ON) {

                LocationDB pdb = new LocationDB(context);
                pdb.processLocation();
                pdb.close();

            }

            if (counter % 10 == 0) {
                counter = 0;
                if (Common.NETWORK_ON )
                    Log.e("WHAT","I AM HERE 1");
                    sendGPS("ping");
            }
        }

        // Restart the location service if it dies
        if(LocationService.updated == 0 || LocationService.updated + 120000 < System.currentTimeMillis()) {
            // Start the location services
            Intent startServiceIntent = new Intent(context, LocationService.class);
            context.startService(startServiceIntent);
        }

    }

    private void sendGPS(final String reason) {

        try {
            LocationDB locationDB = new LocationDB(context);
            JSONArray array = new JSONArray(locationDB.getJSON(LocationDB.TABLE_LOCATION));
            Log.e("WHAT","I AM HERE");
            for (int i = 0; i < array.length(); i++) {

                try {
                    final JSONObject object = array.getJSONObject(i);
                    final String name = object.getString("name");
                    final String created_date = object.getString("created_date");
                    final String gps_lat = object.getString("gps_lat");
                    final String gps_long = object.getString("gps_long");
                    final String user_uid = object.getString("user_uid");
                    final String parcel_uid = object.getString("parcel_uid");
                    final String parcel_status = object.getString("parcel_status");
                    if(parcel_status.equals("In Progress")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        String dateTime = sdf.format(Calendar.getInstance().getTime());
                        Locations locations = new Locations(name, dateTime, parcel_uid, user_uid, Common.GPS_LAT, Common.GPS_LONG, parcel_status);
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();//.getReference("Shipper");
                        DatabaseReference mDatabase = firebaseDatabase.getReference("Location");
                        mDatabase.child(String.valueOf(System.currentTimeMillis())).setValue(locations).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.e("Success", " U Won");
                                } else {
                                    Log.e("Error", " error");
                                }
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            locationDB.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        .addBodyParameter("lat", Config.GPS_LAT.toString())
//                .addBodyParameter("lng", Config.GPS_LONG.toString())
        //if(Config.GPS_LONG != 0.0 && Config.GPS_LAT != 0.0) {
    }
}
