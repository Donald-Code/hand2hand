package tshabalala.bongani.courierservice.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
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

import androidx.annotation.Nullable;
import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.helper.LocationDB;
import tshabalala.bongani.courierservice.model.Locations;

public class LocationService extends Service implements LocationListener {

    public static long updated = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updated = System.currentTimeMillis();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGPSEnabled) {
            Log.i("TAG", "Locations Provider: GPS");
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            lm.requestLocationUpdates(lm.getBestProvider(criteria, true), 20000, 0, this);
            //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, this);
        } else if (isNetworkEnabled) {
            Log.i("TAG", "Locations Provider: NETWORK");
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, this);
        } else {
            Log.i("TAG", "Locations Provider: NONE");
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }
    // Implementing the location listener requires these
    @Override
    public void onLocationChanged(Location l) {
        Common.GPS_LAT = l.getLatitude();
        Common.GPS_LONG = l.getLongitude();
        Log.d("TAG", "location changed: lat="+Common.GPS_LAT+", lon="+Common.GPS_LONG);
        updated = System.currentTimeMillis();

        startLocation();
    }

    private void startLocation() {

        try {
            LocationDB locationDB = new LocationDB(Common.TAG);
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
                    final String parcel_status = object.getString("status");
                    Log.e("WHAT"," Status " + parcel_status);
                    if(parcel_status.trim().equals("In Progress")) {
                        Log.e("WHAT"," 1 ");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        Log.e("WHAT"," 2 ");
                        String dateTime = sdf.format(Calendar.getInstance().getTime());
                        Log.e("WHAT"," 3");
                        Locations locations = new Locations(name, dateTime, parcel_uid, user_uid, Common.GPS_LAT, Common.GPS_LONG, parcel_status);
                        Log.e("WHAT"," location " + locations);
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();//.getReference("Shipper");
                        DatabaseReference mDatabase = firebaseDatabase.getReference("Location");
                        Log.e("WHAT"," mDatabasem " + mDatabase);
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
    }

    public void onProviderDisabled(String arg0) {
        Log.d("TAG", "GPS provider disabled " + arg0);
    }

    public void onProviderEnabled(String arg0) {
        Log.d("TAG", "provider enabled " + arg0);
    }
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        Log.d("TAG", "status changed to " + arg0 + " [" + arg1 + "]");
    }
}
