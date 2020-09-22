package tshabalala.bongani.courierservice;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.helper.LocationDB;
import tshabalala.bongani.courierservice.helper.MenuViewHolder;
import tshabalala.bongani.courierservice.model.Locations;
import tshabalala.bongani.courierservice.model.Parcel;
import tshabalala.bongani.courierservice.model.User;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class TestingMap extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener{
    private MapView mapView;
    private MapboxMap mapboxMap;
    Parcel parcel;
    User user;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
   // private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
   // private NavigationMapRoute navigationMapRoute;
    // variables needed to initialize navigation
    private Button button;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference location;
    FirebaseRecyclerAdapter<Locations, MenuViewHolder> adapterLocation;
    List<Locations> locationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiZG9za3kiLCJhIjoiY2sxbXU0ZTZlMDA1dzNtb2luaDdjenU1aCJ9.mQJlwIGhqvYRSuqyC96IFA");
        setContentView(R.layout.activity_testing);
        Log.e("TAG", "1 ");
        LocationDB locationDB = new LocationDB(TestingMap.this);
        locationList =  locationDB.getAllLocation("");
        Log.e("TAG", "location "+ locationList.size() + " mhm "+ locationDB.getIncidentCount());
        locationDB.close();
        Log.e("TAG", "2 ");
        firebaseDatabase = FirebaseDatabase.getInstance();
        location = firebaseDatabase.getReference("Location");
        parcel = (Parcel) getIntent().getSerializableExtra("parcel");
        user = (User) getIntent().getSerializableExtra("user");

        loadLocations();

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

       // loadLocations();

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);

                addDestinationIconSymbolLayer(style);
List<MarkerOptions> markerOptions = new ArrayList<>();
                if(locationList.size() > 0){
                    for(Locations locations : locationList) {
                        Log.e("TAG", "ad "+ locations.getLatitude() + " asss "+ locations.getLongitude());
                        if(user.getUid().equals(locations.getUser_uid()) && parcel.getUid().equals(locations.getParcel_uid())){

                            mapboxMap.addMarker(new MarkerOptions().position(new LatLng(locations.getLatitude(),locations.getLongitude())).setTitle(locations.getName()));

                        }
//                        Point destinationPoint = Point.fromLngLat(locations.getLongitude(), locations.getLatitude());
//                        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
//                                locationComponent.getLastKnownLocation().getLatitude());
//
//                        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
//                        if (source != null) {
//                            source.setGeoJson(Feature.fromGeometry(destinationPoint));
//                        }
                    }

                }
               // mapboxMap.addOnMapClickListener(TestingMap.this);
//                button = findViewById(R.id.startButton);
//                button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        boolean simulateRoute = true;
//                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
//                                .directionsRoute(currentRoute)
//                                .shouldSimulateRoute(simulateRoute)
//                                .build();
//// Call this method with Context from within an Activity
//                        NavigationLauncher.startNavigation(MainActivity.this, options);
//                    }
//                });
            }
        });
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }

//        getRoute(originPoint, destinationPoint);
//        button.setEnabled(true);
//        button.setBackgroundResource(R.color.mapboxBlue);
        return true;
    }

    private void loadLocations() {
//        ProgressBar progressBar = findViewById(R.id.progress);
//        progressBar.setVisibility(View.GONE);
        Log.e("TAG","here");
//        FirebaseRecyclerOptions<Locations> options = new FirebaseRecyclerOptions.Builder<Locations>()
//                .setQuery(location, Locations.class).build();
//        adapterLocation = new FirebaseRecyclerAdapter<Locations, MenuViewHolder>(options) {
//            @NonNull
//            @Override
//            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
//                return new MenuViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull final Locations locations) {
//                Log.e("TAG", " NAme " + locations.getName() + " Surnssme " + locations.getParcel_status());
//
//                locationList.add(locations);
//
//                Log.e("TAG"," adada "+ locationList.size());
//
//            }
//        };

        location.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("TAG", "user " + dataSnapshot);
                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                            Log.e("TAG", "user " + snap.getValue());
                            Locations locations = snap.getValue(Locations.class);
                            locationList.add(locations);
                        }
//                        if (dataSnapshot.exists()) {
//
//                            Log.e("TAG", "user data " + dataSnapshot);
//                            mUser.setName(String.valueOf(dataSnapshot.child("name").getValue()));
//                            mUser.setSurname(String.valueOf(dataSnapshot.child("surname").getValue()));
//                            mUser.setPhone(String.valueOf(dataSnapshot.child("phone").getValue()));
//                            mUser.setEmail(String.valueOf(dataSnapshot.child("email").getValue()));
//                            mUser.setUid(uid);
//                            mUser.setRole(String.valueOf(dataSnapshot.child("role").getValue()));
//                            mUser.setDateofbirth(String.valueOf(dataSnapshot.child("dateofbirth").getValue()));
//                            mUser.setGender(String.valueOf(dataSnapshot.child("gender").getValue()));
//                            mUser.setAge(String.valueOf(dataSnapshot.child("age").getValue()));
//                            Common.currentUser = mUser;
//                            Intent intent = new Intent(SignIn.this, AdminActivity.class);
//                            intent.putExtra("user",mUser);
//                            startActivity(intent);
//                            Log.e("TAG", "user error " + dataSnapshot);
//                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("TAG", "user " + databaseError.getMessage());
                    }
                }
        );

        //recy.setAdapter(adapterCustomer);
    }

//    private void getRoute(Point origin, Point destination) {
//        NavigationRoute.builder(this)
//                .accessToken(Mapbox.getAccessToken())
//                .origin(origin)
//                .destination(destination)
//                .build()
//                .getRoute(new Callback<DirectionsResponse>() {
//                    @Override
//                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//// You can get the generic HTTP info about the response
//                        Log.d(TAG, "Response code: " + response.code());
//                        if (response.body() == null) {
//                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
//                            return;
//                        } else if (response.body().routes().size() < 1) {
//                            Log.e(TAG, "No routes found");
//                            return;
//                        }
//
//                        currentRoute = response.body().routes().get(0);
//
//// Draw the route on the map
//                        if (navigationMapRoute != null) {
//                            navigationMapRoute.removeRoute();
//                        } else {
//                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
//                        }
//                        navigationMapRoute.addRoute(currentRoute);
//                    }
//
//                    @Override
//                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
//                        Log.e(TAG, "Error: " + throwable.getMessage());
//                    }
//                });
//    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
// Activate the MapboxMap LocationComponent to show user location
// Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (adapterLocation != null) {
            adapterLocation.startListening();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        if (adapterLocation != null) {
            adapterLocation.stopListening();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}

