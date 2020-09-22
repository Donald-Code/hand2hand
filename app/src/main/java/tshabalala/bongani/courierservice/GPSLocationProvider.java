package tshabalala.bongani.courierservice;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import tshabalala.bongani.courierservice.helper.Common;

class GPSLocationProvider {

    static class GPSCoordinates {
        float longitude = -1;
        float latitude = -1;

        GPSCoordinates(double theLatitude, double theLongitude) {
            longitude = (float) theLongitude;
            latitude = (float) theLatitude;
        }
    }

    interface LocationCallback {
        void onNewLocationAvailable(GPSCoordinates location);
    }

    static void requestLastKnown(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location temp_loc_gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location temp_loc_network = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (temp_loc_gps != null) {
            Common.GPS_LAT = temp_loc_gps.getLatitude();
            Common.GPS_LONG = temp_loc_gps.getLongitude();
        } else if(temp_loc_network != null) {
            Common.GPS_LAT = temp_loc_network.getLatitude();
            Common.GPS_LONG = temp_loc_network.getLongitude();
        } else {
            Common.GPS_LAT = 0.0;
            Common.GPS_LONG = 0.0;
        }
        Log.d("TAG","LAST KNOWN GPS Lat:" + Common.GPS_LAT + " long: " + Common.GPS_LONG);
    }

    // calls back to calling thread, note this is for low grain: if you want higher precision, swap the
    // contents of the else and if. Also be sure to check gps permission/settings are allowed.
    // call usually takes <10ms
    static void requestSingleUpdate(final Context context, final LocationCallback callback) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            Log.d("TAG","Using GPS to update location");
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    callback.onNewLocationAvailable(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                }

                @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
                @Override public void onProviderEnabled(String provider) { }
                @Override public void onProviderDisabled(String provider) { }
            }, null);
        } else {
            if (isNetworkEnabled) {
                Log.d("TAG","Using Network to get location");
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                locationManager.requestSingleUpdate(criteria, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        callback.onNewLocationAvailable(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                    }

                    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
                    @Override public void onProviderEnabled(String provider) { }
                    @Override public void onProviderDisabled(String provider) { }
                }, null);
            }
        }
    }
}
