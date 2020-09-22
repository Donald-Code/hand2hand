package tshabalala.bongani.courierservice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import tshabalala.bongani.courierservice.fragments.AboutFragment;
import tshabalala.bongani.courierservice.fragments.CurrentOrderFragment;
import tshabalala.bongani.courierservice.fragments.NewOrderFragment;
import tshabalala.bongani.courierservice.fragments.PreviousOrdersFragment;
import tshabalala.bongani.courierservice.fragments.PricelistFragment;
import tshabalala.bongani.courierservice.fragments.ProfileEditFragment;
import tshabalala.bongani.courierservice.fragments.ProfileFragment;
import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.helper.LocationDB;
import tshabalala.bongani.courierservice.helper.MenuViewHolder;
import tshabalala.bongani.courierservice.helper.MyDividerItemDecoration;
import tshabalala.bongani.courierservice.interfaces.ItemClickListener;
import tshabalala.bongani.courierservice.model.Locations;
import tshabalala.bongani.courierservice.model.Notify;
import tshabalala.bongani.courierservice.model.Parcel;
import tshabalala.bongani.courierservice.model.Shipper;
import tshabalala.bongani.courierservice.model.User;
import tshabalala.bongani.courierservice.services.LocationService;
import tshabalala.bongani.courierservice.services.MyFirebaseMessagingService;

import static android.widget.LinearLayout.VERTICAL;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private static final String PREFERENCES_NAME = "SharePref";
    private static final String PREFERENCES_TEXT_FIELD = "orderTimeStamp";
    private static final String PREFERENCES_EMPTY = "empty";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    DrawerLayout drawer;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference parcels;
    DatabaseReference notify;
    DatabaseReference location;
    FirebaseRecyclerAdapter<Notify, MenuViewHolder> adapterNotify;
    FirebaseRecyclerAdapter<Parcel, MenuViewHolder> adapterParcel;
    TextView textViewName;
    RecyclerView recyclerView_menu;
    private boolean isSinglePressed;
    SwipeRefreshLayout swipeRefreshLayout;
    User user;
    private MaterialSpinner parcelStatus;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int LOCATION_PERMISSION_REQUEST = 1001;
    private final static int WRITE_PERMISSION_REQUEST = 1002;
    private final static int READ_PERMISSION_REQUEST = 1003;

    private Location mLastLocation;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private static int UPDATE_INTERVAL = 1000;
    private static int FASTEST_INTERVAL = 1000;
    private static int DISPLACEMENT = 1000;

    private int mCounter = 0;
    private Handler mHandler = new Handler();

    private Runnable mResetCounter = new Runnable() {
        @Override
        public void run() {
            mCounter = 0;
        }
    };


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            Common.TAG = this;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestRunTimePermission();
            } else {
                if (checkPlayServices()) {
                    buildGoogleApiClient();
                    createLocationRequest();
                    GPSLocationProvider.requestLastKnown(MainActivity.this);

                    if (Common.STARTUP) {
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
                                        bt.setContext(MainActivity.this.getApplicationContext());
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
                }
            }


            //Set firebase
            user = (User) getIntent().getSerializableExtra("user");
            recyclerView_menu = findViewById(R.id.recycler_menu);

            firebaseDatabase = FirebaseDatabase.getInstance();

            parcels = firebaseDatabase.getReference("Parcel");
            notify = firebaseDatabase.getReference("Notify");
            location = firebaseDatabase.getReference("Location");

            Log.e("User Role ", "role " + user.getRole());
            // Set SharedPreferences
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ShipperLocationActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });

            recyclerView_menu = findViewById(R.id.recycler_menu);

            swipeRefreshLayout = findViewById(R.id.swipeHome);

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
            textViewName.setText(user.getName());
            textViewEmail.setText(user.getEmail());
            Picasso.with(MainActivity.this).load(user.getImage()).into(profilePic);



         //   mAuth = FirebaseAuth.getInstance();

            //Load Menu
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

            recyclerView_menu.setLayoutManager(mLayoutManager);
            recyclerView_menu.setItemAnimator(new DefaultItemAnimator());
            recyclerView_menu.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
            //recyclerView_menu.setLayoutManager(new GridLayoutManager(this, 2));
            if (user.getRole().equals("Customer")) {
                loadNotifyMenu();
            } else {
                loadParcelMenu();
            }

        }
        //Helper Method
        private void loadNotifyMenu() {
            ProgressBar progressBar = findViewById(R.id.progress);
            progressBar.setVisibility(View.GONE);
            Log.w("TAG", "1 ");
            FirebaseRecyclerOptions<Notify> options = new FirebaseRecyclerOptions.Builder<Notify>()
                    .setQuery(notify, Notify.class).build();
            Log.w("TAG", "options " + options);
            ObservableSnapshotArray array = options.getSnapshots();
            Log.w("TAG", "array " + array.toString());
            adapterNotify = new FirebaseRecyclerAdapter<Notify, MenuViewHolder>(options) {
                @NonNull
                @Override
                public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_main, parent, false);
                    return new MenuViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull final Notify shipper) {
                    TextView textViewName = holder.itemView.findViewById(R.id.name);
                    TextView textViewPhone = holder.itemView.findViewById(R.id.phone);
                    TextView textViewFrom = holder.itemView.findViewById(R.id.from);
                    TextView textViewTo = holder.itemView.findViewById(R.id.to);
                    TextView textViewDate = holder.itemView.findViewById(R.id.date);
                    Log.w("TAG", "shipper " + shipper);
                  //  if(shipper.getFrom())

                    textViewName.setText("Name : "+shipper.getName() + " \t" + shipper.getSurname());
                    textViewPhone.setText("Phone : "+shipper.getPhone());
                    textViewFrom.setText("Starting from : " + shipper.getFrom() + " >>>>> " + shipper.getDestination());
                    textViewTo.setText(shipper.getDate());
                    // textViewDate.setText(shipper.getDate());

                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onclick(View view, int position, boolean isLongClick) {
                            //We need to send CategoryId to the next sub-activity
                            Intent intentFood = new Intent(MainActivity.this, ParcelActivity.class);
                            intentFood.putExtra("user", user);
                            intentFood.putExtra("notify", shipper);
                            startActivity(intentFood);
                        }
                    });
                }
            };

            recyclerView_menu.setAdapter(adapterNotify);
        }

        private void loadParcelMenu() {
            ProgressBar progressBar = findViewById(R.id.progress);
            progressBar.setVisibility(View.GONE);
            Log.w("TAG", "1 ");
            FirebaseRecyclerOptions<Parcel> options = new FirebaseRecyclerOptions.Builder<Parcel>()
                    .setQuery(parcels, Parcel.class).build();
            adapterParcel = new FirebaseRecyclerAdapter<Parcel, MenuViewHolder>(options) {
                @NonNull
                @Override
                public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parcel_dialog_main, parent, false);
                    return new MenuViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull final Parcel parcel) {
                    TextView textViewName = holder.itemView.findViewById(R.id.name);
                    TextView textViewPhone = holder.itemView.findViewById(R.id.phone);
                    TextView textViewFrom = holder.itemView.findViewById(R.id.from);
                    TextView textViewTo = holder.itemView.findViewById(R.id.to);
                    TextView textViewPrice = holder.itemView.findViewById(R.id.price);
                    TextView textViewStatus = holder.itemView.findViewById(R.id.status);
                    Log.w("TAG", "parcel " + parcel.getStatus());


                    textViewName.setText(parcel.getName());
                    textViewPhone.setText(parcel.getPhone());
                    textViewFrom.setText("From : " + parcel.getPickup() + " >>>>> " + parcel.getDestination());
                    // textViewTo.setText(parcel.getDestination());
                    textViewStatus.setText(parcel.getStatus());
                    textViewPrice.setText("R " + parcel.getPrice() + "");

                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onclick(View view, int position, boolean isLongClick) {

                            showUpdateDialog(parcel.getUid(), parcel);
                            //We need to send CategoryId to the next sub-activity
//                        Intent intentFood = new Intent(HomeActivity.this, FoodActivity.class);
//                        intentFood.putExtra("categoryId", adapter.getRef(position).getKey());
//                        startActivity(intentFood);
                        }
                    });
                }
            };

            recyclerView_menu.setAdapter(adapterParcel);
        }

        private void showUpdateDialog(final String key, final Parcel parcel) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Parcel Options");
           // alertDialog.setMessage("Choose status");

            @SuppressLint("InflateParams")
            View view = getLayoutInflater().inflate(R.layout.accept_parcel_dialog, null);
            alertDialog.setView(view);

            parcelStatus = view.findViewById(R.id.parcel_status);

            parcelStatus.setItems("Accept", "Decline", "Counter Offer");
//            parcelStatus.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
//
//                @Override public void onItemSelected(MaterialSpinner view, int position, long id, String option) {
//                   // Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
//
//                }
//            });

            alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    int option = parcelStatus.getSelectedIndex();
                    if (option == 0) {

                        changeStatus(parcel.getName(), "Accept");
                        startSendingGPSCoordinates(parcel.getName(), parcel.getUid(), user.getUid());
                    }else if(option == 1){

                        changeStatus(parcel.getName(), "Decline");

                    } else if(option == 2) {

                        showAmountDialog(parcel.getName(), "Counter Offer");
                        // changeStatus(parcel.getUid(), option);

                    }
                    Log.e("TAG", " options " + String.valueOf(parcelStatus.getSelectedIndex()));
                }
            });


            alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            alertDialog.show();
        }

        private void showAmountDialog(String uid, String answer) {

            View view = LayoutInflater.from(this).inflate(R.layout.counter_offer_dialog, null);

            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilderUserInput.setView(view);

            final EditText inputAmount = view.findViewById(R.id.amount);

            alertDialogBuilderUserInput
                    .setTitle("Counter Offer")
                    .setCancelable(false)
                    .setPositiveButton("Send", (dialogBox, id) -> {

                    })
                    .setNegativeButton("cancel",
                            (dialogBox, id) -> dialogBox.cancel());

            final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputAmount.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Amount cant be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                counterOffers(uid, answer, Integer.parseInt(inputAmount.getText().toString()));


            });
        }

        private void startSendingGPSCoordinates(String name, String parcel_uid, String user_uid) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String dateTime = sdf.format(Calendar.getInstance().getTime());
            Locations loc = new Locations(name, dateTime, parcel_uid, user_uid, Common.GPS_LAT, Common.GPS_LONG, " In Progress");
            LocationDB locationDB = new LocationDB(MainActivity.this);
            locationDB.addLocation(loc);
            locationDB.close();
            location.child(String.valueOf(System.currentTimeMillis())).setValue(loc).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.e("ADD", " Added");
                    } else {
                        Log.e("Error", " error");
                    }
                }
            });


        }

        private void changeStatus(String key, String option) {
            Query editQuery = parcels.orderByChild("name").equalTo(key);
            editQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot edtData: dataSnapshot.getChildren()){
                        edtData.getRef().child("status").setValue(option);
                    }
                    Toast.makeText(MainActivity.this,"Status changed",Toast.LENGTH_LONG).show();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
            //parcels.child("status").setValue(option);
           // parcels.setValue(option);
        }


        private void counterOffers(String key, String option, int amount) {
            parcels.child(key).child("status").setValue(option);
            parcels.child(key).child("amount").setValue(amount);
            Toast.makeText(MainActivity.this," Updated successfully",Toast.LENGTH_SHORT).show();
        }

        private void saveData(String data) {
            SharedPreferences.Editor preferencesEditor = mSharedPreferences.edit();
            preferencesEditor.putString(PREFERENCES_TEXT_FIELD, data);
            preferencesEditor.commit();
        }

        @Override
        public void onStart() {
            super.onStart();
            if (googleApiClient != null) {
                googleApiClient.connect();
            }

            if (adapterNotify != null) {
                adapterNotify.startListening();
            }
            if (adapterParcel != null) {
                adapterParcel.startListening();
            }
            //  adapterParcel.startListening();
            // mAuth.addAuthStateListener(mAuthListener);
        }

        @Override
        public void onStop() {
            super.onStop();
            if (adapterNotify != null) {
                adapterNotify.stopListening();
            }
            if (adapterParcel != null) {
                adapterParcel.stopListening();
            }

        }

        @Override
        protected void onResume() {
            checkPlayServices();
            super.onResume();
            String dateString;
            if (BackgroundTask.last_run != 0) {
                dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date(BackgroundTask.last_run));
            } else {
                dateString = "Not set!";
            }
            Log.d("TAG", "Background service last run @ " + dateString);
            if (BackgroundTask.last_run < System.currentTimeMillis() - 300000)
                MainActivity.this.recreate(); // restart main if background not ran in 5 mins.
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
            // selectDrawerItem(item);

            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                // Handle user profile
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);

            } else if (id == R.id.nav_logout) {
                //clearing remember me data in PaperDb
                startActivity(new Intent(MainActivity.this, SignIn.class));
                finish();
                mAuth.signOut();
            } else if (id == R.id.nav_profileedit) {
                //clearing remember me data in PaperDb
                Intent intent = new Intent(MainActivity.this, ProfileEditActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            } else if (id == R.id.nav_my_places) {
                //clearing remember me data in PaperDb
                Intent intent = new Intent(MainActivity.this, MyPlaces.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }


            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
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
                        }
                    }
                    break;

            }
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;
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

        private void startLocationUpdates() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLastLocation = location;
                }
            });
        }


        //Helper Methods
        private void requestRunTimePermission() {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
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
            if (resultCode != ConnectionResult.SUCCESS) {
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return false;
            }
            return true;
        }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    }
