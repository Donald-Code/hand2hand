package tshabalala.bongani.courierservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tshabalala.bongani.courierservice.helper.LocationDB;
import tshabalala.bongani.courierservice.model.Locations;

public class MarkerActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnInfoWindowCloseListener {

    private GoogleMap mMap;
    ArrayList<LatLng> points;
    GoogleApiClient mGoogleApiClient;

    private static final String URL_REGISTER_ORDINATE = "http://apolloapollo.000webhostapp.com/getCoordinates.php";
    private static final String TAG_COORDINATE = "coordinates";
    private static final String TAG_SUCCESS = "success";
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 10000;
    List<LatLng> cordinatesList = new ArrayList<>();
    // List<UserLocation> detailsList = new ArrayList<>();


    private final List<Marker> mMarkerRainbow = new ArrayList<Marker>();

    private TextView mTopText;

    private SeekBar mRotationBar;

    private CheckBox mFlatBox;
    Geocoder geocoder;
    String email;
    String name;
    String phonenumber;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference locations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_demo);
        //Set firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        locations = firebaseDatabase.getReference("Location");
        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Apollo");
        // setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // points = new ArrayList<LatLng>();


        mTopText = (TextView) findViewById(R.id.top_text);

        mRotationBar = (SeekBar) findViewById(R.id.rotationSeekBar);
        mRotationBar.setMax(360);
        mRotationBar.setOnSeekBarChangeListener(this);

        mFlatBox = (CheckBox) findViewById(R.id.flat);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        phonenumber = getIntent().getStringExtra("chatWith");
        name = getIntent().getStringExtra("name");

        loadCoordinates();


    }

    private void loadCoordinates() {
        final ProgressDialog progressDialog = new ProgressDialog(MarkerActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        LocationDB locationDB = new LocationDB(MarkerActivity.this);
        List<Locations> locationList =  locationDB.getAllLocation("");
        Log.e("TAG", "location "+ locationList.size() + " mhm "+ locationDB.getIncidentCount());
        locationDB.close();
        Log.e("TAG", "2 ");

        locations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Checking User avail
                try {


                    progressDialog.dismiss();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.w("TAG", "snapshot " + snapshot);
                        Log.w("TAG", "snapshot " + snapshot.getValue().toString());
                        String username = snapshot.getKey().substring(0, snapshot.getKey().indexOf("-"));
                        if (username.equals(phonenumber)) {
                            Location userLocation = snapshot.getValue(Location.class);
                            if (userLocation != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                try {
                                    // Date date1 = sdf.parse(userLocation.getLocationDate());
                                    Date date2 = sdf.parse(currentDate());
//
//                                    if (date1.before(date2)) {
//                                        System.out.println("");
//                                        locations.child(snapshot.getKey()).removeValue();
//                                        Log.w("TAG", "Date1 is before Date2 ");
//                                    }
//
//                                    if (date1.equals(date2)) {
//                                        detailsList.add(userLocation);
//                                        Log.w("TAG", "Date1 is equal to Date2 ");
//                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                            }
                        }

                    }
                    //      Log.w("TAG", "list size " + detailsList.size());
//                    try {
//                      //  doOnSuccess(detailsList);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                } catch (Exception x) {
                    progressDialog.dismiss();
//                    Toast.makeText(MarkerActivity.this, "There is no coordinates for this user", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(MarkerActivity.this,Chat.class);
//                    intent.putExtra("friendName", name);
//                    intent.putExtra("friendNumber", phonenumber);
//                    startActivity(intent);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MarkerActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });

    }

    private String currentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(c.getTime());
    }

    public String currentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(c.getTime());
    }

    int totalUsers = 0;

    private void doOnSuccess(List<Location> detailsList) throws IOException {
        // Log.w("TAG", "userLocation " + userLocation.getLocationAddress());//new Details(addresses.get(0).getLatitude(),addresses.get(0).getLongitude(),userLocation.getLocationTime()));
        totalUsers++;
        Log.w("TAG", "detailsList " + detailsList.size());
        if (detailsList.size() > 0) {

            //  addMarkersToMap(detailsList);
        } else if (detailsList.size() == 0) {
            Toast.makeText(MarkerActivity.this, "No location for user", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);

            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }

        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Add lots of markers to the map.
        //  addMarkersToMap();

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.setOnInfoWindowLongClickListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        mMap.setContentDescription("Map with lots of markers.");

//        LatLngBounds bounds = new LatLngBounds.Builder()
//                .include(PERTH)
//                .include(SYDNEY)
//                .include(ADELAIDE)
//                .include(BRISBANE)
//                .include(MELBOURNE)
//                .build();
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //String address,city,time;

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //intent your parent activity
//            Intent intent = new Intent(MapsActivity.this,Chat.class);
//            intent.putExtra("email",email);
//            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    private void addMarkersToMap(List<Location> userLocations) {

        try {

            final ProgressDialog pd = new ProgressDialog(MarkerActivity.this);
            pd.setMessage("Loading location...");
            pd.show();

            float rotation = mRotationBar.getProgress();
            boolean flat = mFlatBox.isChecked();

            geocoder = new Geocoder(this, Locale.getDefault());


            // Instantiating the class PolylineOptions to plot polyline in the map
            PolylineOptions polylineOptions = new PolylineOptions();

            // Setting the color of the polyline
            polylineOptions.color(Color.RED);

            // Setting the width of the polyline
            polylineOptions.width(5);

            List<LatLng> lngList = new ArrayList<>();
            int numMarkersInRainbow = userLocations.size();
            Log.w("Tag", " numMarkersInRainbow size " + numMarkersInRainbow);
            // for (int i = 0; i < numMarkersInRainbow; i++) {

            for (Location location : userLocations) {
                // Log.w("Tag"," addresses latLngLost.get(i) "+ location.getLocationAddress());
                List<Address> addresses = null;
                try {

                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Log.w("Tag", " addresses size " + addresses.size());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                Log.w("Tag", " address " + address + " city " + city);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                lngList.add(latLng);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(city + "\n" + address)
                        //  .snippet(location.getLocationTime())
                        .icon(BitmapDescriptorFactory.defaultMarker())//.defaultMarker(i * 360 / numMarkersInRainbow))
                        .flat(flat)
                        .rotation(rotation));

                mMarkerRainbow.add(marker);
                //    marker.showInfoWindow();

//            LatLngBounds bounds = new LatLngBounds.Builder()
//                    .include(latLngLost.get(0))
//                    .build();
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17.0f).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //  mMap.setCame\ .moveCamera(CameraUpdateFactory.newLatLngZoom(,15.0f));
//                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//                        @Override
//                        public View getInfoWindow(Marker marker) {
//                            return null;
//                        }
//
//                        @Override
//                        public View getInfoContents(Marker marker) {
//                            marker.showInfoWindow();
//                            return null;
//                        }
//                    });

                polylineOptions.addAll(lngList);


            }
            mMap.addPolyline(polylineOptions);

            pd.dismiss();
        } catch (Exception s) {
            s.getStackTrace();
        }

    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, "map not ready", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Called when the Clear button is clicked.
     */
    public void onClearMap(View view) {
        if (!checkReady()) {
            return;
        }
        mMap.clear();
    }

    /**
     * Called when the Reset button is clicked.
     */
    public void onResetMap(View view) {
        if (!checkReady()) {
            return;
        }
        // Clear the map because we don't want duplicates of the markers.
        mMap.clear();
        // addMarkersToMap(detailsList);
    }

    /**
     * Called when the Reset button is clicked.
     */
    public void onToggleFlat(View view) {
        if (!checkReady()) {
            return;
        }
        boolean flat = mFlatBox.isChecked();
        for (Marker marker : mMarkerRainbow) {
            marker.setFlat(flat);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!checkReady()) {
            return;
        }
        float rotation = seekBar.getProgress();
        for (Marker marker : mMarkerRainbow) {
            marker.setRotation(rotation);
            //  marker.showInfoWindow();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    //
    // Marker related listeners.
    //


    @Override
    public boolean onMarkerClick(final Marker marker) {
        // if (marker.equals()) {
        // This causes the marker at Perth to bounce into position when it is clicked.
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final BounceInterpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 2 * t);

                if (t > 0.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
        //}

        // Markers have a z-index that is settable and gettable.

        float zIndex = marker.getZIndex() + 1.0f;
        marker.setZIndex(zIndex);
        // Toast.makeText(this, marker.getTitle() + " z-index set to " + zIndex, Toast.LENGTH_SHORT).show();
        Log.w("TAG", "Maps Position " + marker.getPosition());
        gotoOption(marker.getPosition(), marker.getTitle(), marker.getSnippet());

        // mLastSelectedMarker = marker;
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        //Toast.makeText(this, "Close Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        // Toast.makeText(this, "Info Window long click", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void gotoOption(LatLng position, String title, String snippet) {
//        Intent intent = new Intent(MapsActivity.this, StreetViewActivity.class);
//        intent.putExtra("position",position);
//        startActivity(intent);

    }
}