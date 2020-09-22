package tshabalala.bongani.courierservice.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import tshabalala.bongani.courierservice.R;
import tshabalala.bongani.courierservice.presenters.NewOrderPresenter;


public class NewOrderFragment extends Fragment  {

    private MapView mMapView;
    private NewOrderPresenter mPresenter;
    private GoogleMap mGoogleMap;
    private TextView mDistanceView;
    private Button mCalculateButton;
    private Button mCallButton;
    private Button mAdditionalButton;
    private TextView placesFrom;
    private TextView placesTo;
    private Intent mOnActivityResultIntent;

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    // map embedded in the map fragment
    private Map map = null;

   //  map fragment embedded in this activity
    private SupportMapFragment mapFragment = null;


    public NewOrderFragment() {
    }

    public static Fragment newInstance() {
        return new NewOrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_neworder, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        checkPermissions();
        mPresenter = new NewOrderPresenter(this);
        mPresenter.setGMapsUrl(getString(R.string.google_maps_key));
        mPresenter.sendFragmentActivityToInteractor();
//
//        mDistanceView = (TextView) view.findViewById(R.id.neworder_textview_summary);
//        mCalculateButton = (Button) view.findViewById(R.id.neworder_button_calculate);
//        mAdditionalButton = (Button) view.findViewById(R.id.neworder_button_additional);
//        mCallButton = (Button) view.findViewById(R.id.neworder_button_call);
//        placesFrom = (TextView) view.findViewById(R.id.place_autocomplete_from);
//        placesTo = (TextView) view.findViewById(R.id.place_autocomplete_to);
      //  mMapView = (MapView) view.findViewById(R.id.map);
      //  mMapView.onCreate(savedInstanceState);

      //  mPresenter.onMapAsyncCall();
       // setListeners();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mOnActivityResultIntent = data;
        mPresenter.onActivityResultCalled(requestCode, resultCode);
    }

    public Intent getOnActivityResultIntent() {
        return mOnActivityResultIntent;
    }

    public void setDistanceView(String distance) {
        mDistanceView.setText(getString(R.string.neworder_summary_start) + " " + distance +
        getString(R.string.neworder_summary_mid));
    }

    public void setFromText(String address) {
        placesFrom.setText(address);
    }

    public void setToText(String address) {
        placesTo.setText(address);
    }

    public void moveCameraTo(double lat, double lng) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(10)
                .build()));
    }

    private void initialize() {


        // Search for the map fragment to finish setup by calling init().
//        mapFragment = getActivity().getSupportMapFragment();
//        mapFragment.init(new OnEngineInitListener() {
//            @Override
//            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
//                if (error == OnEngineInitListener.Error.NONE) {
//                    // retrieve a reference of the map from the map fragment
//                    map = mapFragment.getMap();
//                    // Set the map center to the Vancouver region (no animation)
//                    map.setCenter(new GeoCoordinate(49.196261, -123.004773, 0.0),
//                            Map.Animation.NONE);
//                    // Set the zoom level to the average between min and max
//                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
//                } else {
//                    Log.e("LOG_TAG", "Cannot initialize SupportMapFragment (" + error + ")");
//                }
//            }
//        });
    }

    // STARTING AGAIN FFS

    private void setListeners() {
        placesFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForPlaceAutocompleteCall(1);
            }
        });
        placesTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForPlaceAutocompleteCall(2);
            }
        });
        mAdditionalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForAdditionalServices();
            }
        });
        mCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForCalculate();
            }
        });
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForCall();
            }
        });
    }

    public FragmentActivity getParentActivity() {
        return getActivity();
    }

//    public void mapAsyncCall() {
//        mMapView.getMapAsync(this);
//    }
//
//    public void setMapClear() {
//        mGoogleMap.clear();
//    }
//
//    public void onMapPolylineAdded(PolylineOptions polylineOptions) {
//        mGoogleMap.addPolyline(polylineOptions);
//    }
//
//    public void animateCamera(CameraUpdate cu) {
//        mGoogleMap.moveCamera(cu);
//        mGoogleMap.animateCamera(cu, 1000, null);
//    }
//
//    public void makeToast(String message) {
//        Toast toast = Toast.makeText(getContext(),
//                message, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();
//    }

    public void showAlertDialog(AlertDialog.Builder builder) {
        builder.show();
    }

//    @Override
//    public void onMapReady(GoogleMap map) {
//        mGoogleMap = map;
//        mPresenter.setMapView();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mMapView.onResume();
//    }
//
//    @Override
//    public final void onDestroy() {
//        super.onDestroy();
//        mMapView.onDestroy();
//        mPresenter.detachView();
//    }
//
//    @Override
//    public final void onLowMemory() {
//        super.onLowMemory();
//        mMapView.onLowMemory();
//    }
//
//    @Override
//    public final void onPause() {
//        super.onPause();
//        mMapView.onPause();
//    }

    /**
     * Checks the dynamically controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(getContext(), "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }



}
