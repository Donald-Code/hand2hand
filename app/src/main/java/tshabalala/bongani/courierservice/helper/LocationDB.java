package tshabalala.bongani.courierservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import tshabalala.bongani.courierservice.AdminActivity;
import tshabalala.bongani.courierservice.RegisterAdminActivity;
import tshabalala.bongani.courierservice.model.Locations;

public class LocationDB extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "locationDB";

    // TABLES
    public static final String TABLE_LOCATION = "location";

    // Table Columns name
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CREATED_DATE = "created_date";
    private static final String KEY_GPS_LAT = "gps_lat";
    private static final String KEY_GPS_LONG = "gps_long";
    private static final String KEY_PARCEL_UID = "parcel_uid";
    private static final String KEY_USER_UID = "user_uid";
    private static final String KEY_STATUS = "status";

    private Context mContext;

    public LocationDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT, "
                + KEY_CREATED_DATE + " TEXT, " + KEY_GPS_LAT + " TEXT, "
                + KEY_GPS_LONG + " TEXT, " + KEY_PARCEL_UID + " TEXT, " + KEY_USER_UID + " TEXT,"
                + KEY_STATUS + " TEXT)";

        Log.d("TAG", CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        // Create tables again
        onCreate(db);
    }

    // Adding new category
    public void addLocation(Locations location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, location.getName());
        values.put(KEY_CREATED_DATE, location.getCreated_date()); // Patrol
        values.put(KEY_GPS_LAT, location.getLatitude()); // Patrol ID
        values.put(KEY_GPS_LONG, location.getLongitude());
        values.put(KEY_PARCEL_UID, location.getParcel_uid());
        values.put(KEY_USER_UID, location.getUser_uid());
        values.put(KEY_STATUS, location.getParcel_status());

        // Inserting Row
        db.insert(TABLE_LOCATION, null, values);
        db.close(); // Closing database connection
    }

    // Deleting single patrol
    private void deleteLocation(String location_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATION, KEY_PARCEL_UID + " = ? AND " + KEY_USER_UID + " = ?",
                new String[]{location_id});
        db.close();
    }

    // Deleting single patrol
    void deleteLegioss() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATION, KEY_ID + " LIKE ?", new String[]{"%"});
        db.close();
    }

    private void setLocationCompleted(String incident_id, String remote_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, "CanDelete");
        db.update(TABLE_LOCATION, contentValues, KEY_ID + " = ?", new String[]{incident_id});
        db.close();
    }

     public List<Locations> getAllLocation(String key) {

        List<Locations> locations = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_LOCATION; //+ " WHERE " + KEY_ID + "=" + key;

        Log.d("TAG",sql);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            locations.add(new Locations(cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_CREATED_DATE)),
                    cursor.getString(cursor.getColumnIndex(KEY_PARCEL_UID)),
                    cursor.getString(cursor.getColumnIndex(KEY_USER_UID)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_GPS_LAT)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_GPS_LONG)),
                    cursor.getString(cursor.getColumnIndex(KEY_STATUS))));
        }
        cursor.close();
        return locations;

    }

    // Total response in the table
    public int getIncidentCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOCATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public String getJSON(String table_name) {

        JSONArray result_set = new JSONArray();
        String selectQuery = "SELECT  * FROM " + table_name;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            int cols = cursor.getColumnCount();
            do {
                JSONObject row = new JSONObject();

                for (int i = 0; i < cols; i++) {
                    if (cursor.getColumnName(i) != null) {
                        try {
                            if (cursor.getString(i) != null) {
                                row.put(cursor.getColumnName(i), cursor.getString(i));
                            } else {
                                row.put(cursor.getColumnName(i), "");
                            }
                        } catch (Exception e) {
                            Log.d("TAG", e.getMessage());
                        }
                    }
                }
                result_set.put(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result_set.toString();
    }

    public void processLocation() {

        try {
            JSONArray array = new JSONArray(this.getJSON(LocationDB.TABLE_LOCATION));
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
