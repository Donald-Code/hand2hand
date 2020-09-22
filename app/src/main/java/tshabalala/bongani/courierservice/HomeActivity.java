package tshabalala.bongani.courierservice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import tshabalala.bongani.courierservice.fragments.AboutFragment;
import tshabalala.bongani.courierservice.fragments.CurrentOrderFragment;
import tshabalala.bongani.courierservice.fragments.NewOrderFragment;
import tshabalala.bongani.courierservice.fragments.PreviousOrdersFragment;
import tshabalala.bongani.courierservice.fragments.PricelistFragment;
import tshabalala.bongani.courierservice.fragments.ProfileEditFragment;
import tshabalala.bongani.courierservice.fragments.ProfileFragment;
import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.helper.MenuViewHolder;
import tshabalala.bongani.courierservice.model.User;
import tshabalala.bongani.courierservice.services.LocationService;
import tshabalala.bongani.courierservice.services.MyFirebaseMessagingService;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int LOCATION_PERMISSION_REQUEST = 1001;

    private Location mLastLocation;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private static int UPDATE_INTERVAL = 1000;
    private static int FASTEST_INTERVAL = 1000;
    private static int DISPLACEMENT = 1000;

    private static final String PREFERENCES_NAME = "SharePref";
    private static final String PREFERENCES_TEXT_FIELD = "orderTimeStamp";
    private static final String PREFERENCES_EMPTY = "empty";
    private SharedPreferences mSharedPreferences;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    DrawerLayout drawer;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference category;
    FirebaseRecyclerAdapter<User, MenuViewHolder> adapter;
    TextView textViewName;
    RecyclerView recyclerView_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestRunTimePermission();
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GPSLocationProvider.requestLastKnown(HomeActivity.this);
        //Set firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        category = firebaseDatabase.getReference("User");

        // Set SharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);//PREFERENCES_NAME, Activity.MODE_PRIVATE);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //Set name on navigation_view header
        View headerView = navigationView.getHeaderView(0);
        TextView textViewName = headerView.findViewById(R.id.textViewName);
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);
        ImageView profilePic = headerView.findViewById(R.id.imageView);
        //  textViewName.setText(Common.currentUser.getName());
        //  textViewEmail.setText(Common.currentUser.getEmail());
        // Picasso.with(MainActivity.this).load(Common.currentUser.getImage()).into(profilePic);

        // Check mode of operation and get config if this is a fresh start
        if(Common.STARTUP ) {
            Common.STARTUP = false;
            // Start the location services
            Intent startServiceIntent = new Intent(this, LocationService.class);
            startService(startServiceIntent);


            final Runnable beeper = new Runnable() {
                public void run() {
                    try {
                        // Required to keep CPU running.
                        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        if (pm != null) {
                            PowerManager.WakeLock wakeLock = pm.newWakeLock(
                                    PowerManager.PARTIAL_WAKE_LOCK, "hand2hand:WakeUp");
                            wakeLock.acquire(600000);
                            BackgroundTask bt = new BackgroundTask();
                            bt.setContext(HomeActivity.this.getApplicationContext());
                            bt.run();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            };

            // Start the thread pool
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            exec.scheduleAtFixedRate(beeper, 0, Common.TRACKING_INTERVAL, TimeUnit.SECONDS);
        }



        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
//                    if(getIntent().getExtras() == null) {
//                        // Begin transaction
//                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                        // Replace the contents of the container with the new fragment
//                        ft.replace(R.id.fragment_placeholder, new NewOrderFragment());
//                        // Complete the changes added above.
//                        ft.commit();
//                    } else {
//                        Bundle extras = getIntent().getExtras();
//                        if(extras.containsKey(MyFirebaseMessagingService.NOTIFICATION_TYPE)) {
//                            String type = extras.getString(MyFirebaseMessagingService.NOTIFICATION_TYPE);
//                            if (type.equals("new") || type.equals("taken")) {
//                                saveData(extras.getString(MyFirebaseMessagingService.NOTIFICATION_TIMESTAMP));
//                                Class fragmentClass = CurrentOrderFragment.class;
//                                Fragment fragment = null;
//                                try {
//                                    fragment = (Fragment) fragmentClass.newInstance();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                                ft.replace(R.id.fragment_placeholder, fragment);
//                                ft.commit();
//                            } else if(type.equals("finished")) {
//                                saveData(PREFERENCES_EMPTY);
//                                // Begin transaction
//                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                                // Replace the contents of the container with the new fragment
//                                ft.replace(R.id.fragment_placeholder, new ProfileFragment());
//                                // Complete the changes added above.
//                                ft.commit();
//                            }
//                        } else {
//                            // Begin transaction
//                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                            // Replace the contents of the container with the new fragment
//                            ft.replace(R.id.fragment_placeholder, new NewOrderFragment());
//                            // Complete the changes added above.
//                            ft.commit();
//                        }
//                    }
                } else {
                    finish();
                    startActivity(new Intent(HomeActivity.this, SignIn.class));
                }
            }
        };

        //Load Menu
        //  recyclerView_menu = findViewById(R.id.my_recycler_view);
//        recyclerView_menu.setHasFixedSize(true);
//        recyclerView_menu.setLayoutManager(new LinearLayoutManager(this, VERTICAL, false));
        //  recyclerView_menu.setLayoutManager(new GridLayoutManager(this, 2));
        // loadMenu();

    }

    private void saveData(String data) {
        SharedPreferences.Editor preferencesEditor = mSharedPreferences.edit();
        preferencesEditor.putString(PREFERENCES_TEXT_FIELD, data);
        preferencesEditor.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume()
    {
        checkPlayServices();
        super.onResume();
//        if (FirebaseDatabase.getInstance() != null)
//        {
//            FirebaseDatabase.getInstance().goOnline();
//        }
        String dateString;
        if(BackgroundTask.last_run != 0) {
            dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date(BackgroundTask.last_run));
        } else {
            dateString = "Not set!";
        }
        Log.d("TAG","Background service last run @ " + dateString);
        if(BackgroundTask.last_run < System.currentTimeMillis() - 300000)
            HomeActivity.this.recreate(); // restart main if background not ran in 5 mins.
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(FirebaseDatabase.getInstance()!=null)
//        {
//            FirebaseDatabase.getInstance().goOffline();
//        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        selectDrawerItem(item);
//        int id = item.getItemId();
//
//        if (id == R.id.nav_profile) {
//            // Handle user profile
//            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//            startActivity(intent);
//        } else if (id == R.id.nav_logout) {
//            //clearing remember me data in PaperDb
//            Paper.book(CLIENT).delete(USER_PHONE);
//            Paper.book(CLIENT).delete(USER_PASSWORD);
//            Paper.book(CLIENT).delete(USER_NAME);
//
//            Intent intent = new Intent(MainActivity.this, ChooseActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//        }
//
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    // Creates a fragment and specifies it (according to selected menuItem), then shows it.
    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_profile:
                fragmentClass = ProfileFragment.class;
                break;
            case R.id.nav_profileedit:
                fragmentClass = ProfileEditFragment.class;
                break;
            case R.id.nav_pricelist:
                fragmentClass = PricelistFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            case R.id.nav_neworder:
                fragmentClass = NewOrderFragment.class;
                break;
            case R.id.nav_current_order:
                fragmentClass = CurrentOrderFragment.class;
                break;
            case R.id.nav_logout:

                startActivity(new Intent(HomeActivity.this, SignIn.class));
                finish();
                mAuth.signOut();
            default:
                fragmentClass = NewOrderFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
       // ft.replace(R.id.fragment_placeholder, fragment).commit();

        // Highlight the selected item
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawer.closeDrawers();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();

                        displaylocation();
                    }
                }
                break;

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displaylocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        displaylocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //Helper Methods
    private void requestRunTimePermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, LOCATION_PERMISSION_REQUEST);
    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        googleApiClient.connect();
    }


    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    private void displaylocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestRunTimePermission();
        }
        else
        {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(mLastLocation != null)
            {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                LatLng yourLocation = new LatLng(latitude, longitude);
               // mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your Locations"));
              //  mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.4233438, -122.0728817))
                        .title("LinkedIn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.4629101,-122.2449094))
                        .title("Facebook")
                        .snippet("Facebook HQ: Menlo Park"));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.3092293, -122.1136845))
                        .title("Apple"));

                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

//                try {
//                    LatLng userLocation = getLocationFromAddress(Common.currentRequest.getAddress());
//                    //Add bitmap to canvas... for later parts
//                    mMap.addMarker(new MarkerOptions().position(userLocation).title(Common.currentRequest.getName() + ", #: "
//                            + Common.currentRequest.getPhone())
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.order)));
//
//                    //draw polyline
//                    GoogleDirection.withServerKey("AIzaSyDkIHeEEiO5VjxKoDI8NNrQnAPov5-vnnk")
//                            .from(yourLocation)
//                            .to(userLocation)
//                            .transportMode(TransportMode.DRIVING)
//                            .execute(new DirectionCallback() {
//                                @Override
//                                public void onDirectionSuccess(Direction direction, String rawBody) {
//                                    if (direction.isOK()) {
//                                        ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).
//                                                getDirectionPoint();
//                                        PolylineOptions polylineOptions = DirectionConverter.createPolyline
//                                                (getApplicationContext(), directionPositionList, 5, Color.BLUE);
//                                        mMap.addPolyline(polylineOptions);
//                                    } else {
//                                        // Do something
//                                        Toast.makeText(TrackingOrderActivity.this, rawBody, Toast.LENGTH_SHORT).show();
//                                        Log.e("key", rawBody);
//                                    }
//                                }
//
//                                @Override
//                                public void onDirectionFailure(Throwable t) {
//                                    Toast.makeText(TrackingOrderActivity.this, "Cannot draw polyline", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//                catch (Exception e)
//                {
//                    Toast.makeText(this, "Order address is not found", Toast.LENGTH_SHORT).show();
//                }
            }
            else
            {
                Toast.makeText(this, "Could not get the last location!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
                displaylocation();
            }
        });
    }


    public LatLng getLocationFromAddress(String strAddress)
    {
        Geocoder geocoder = new Geocoder(this);
        List<Address> address;
        LatLng latLng = null;
        try {
            address = geocoder.getFromLocationName(strAddress, 5);
            if (address == null) {
                Toast.makeText(this, "Cannot get order latlng details", Toast.LENGTH_SHORT).show();
                return null;
            }

            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            latLng = new LatLng(location.getLatitude(),
                    location.getLongitude());

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }


}//class ends.