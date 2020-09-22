package tshabalala.bongani.courierservice;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.helper.LocationDB;
import tshabalala.bongani.courierservice.helper.MenuViewHolder;
import tshabalala.bongani.courierservice.helper.MyDividerItemDecoration;
import tshabalala.bongani.courierservice.helper.SwipeToDeleteCallback;
import tshabalala.bongani.courierservice.interfaces.ItemClickListener;
import tshabalala.bongani.courierservice.model.Locations;
import tshabalala.bongani.courierservice.model.Notify;
import tshabalala.bongani.courierservice.model.Parcel;
import tshabalala.bongani.courierservice.model.Shipper;
import tshabalala.bongani.courierservice.model.User;

public class MyPlaces extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference customer;
    DatabaseReference parcel;
    DatabaseReference location;
    FirebaseRecyclerAdapter<Notify, MenuViewHolder> adapterCustomer;
    FirebaseRecyclerAdapter<Parcel, MenuViewHolder> adapterParcel;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView_menu;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_places);

        user = (User) getIntent().getSerializableExtra("user");

        firebaseDatabase = FirebaseDatabase.getInstance();
        customer = firebaseDatabase.getReference("Notify");
        parcel = firebaseDatabase.getReference("Parcel");
        location = firebaseDatabase.getReference("Location");

        swipeRefreshLayout = findViewById(R.id.swipeHome);
        recyclerView_menu = findViewById(R.id.recycler_menu);

        //Load Menu
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        recyclerView_menu.setLayoutManager(mLayoutManager);
        recyclerView_menu.setItemAnimator(new DefaultItemAnimator());
        recyclerView_menu.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        Log.e("MYPLACES", "1");
        if(user.getRole().equals("Shipper")) {
            loadCustomers();
        }else{
            loadParcels();
        }
        Log.e("MYPLACES", "2");
        enableSwipeToDeleteAndUndo();
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                if(user.getRole().equals("Shipper")) {
                   Notify notify =  adapterCustomer.getItem(i);
                    customer.child(notify.getUid()).removeValue();
                }else {
                    Parcel parce =  adapterParcel.getItem(i);
                    parcel.child(parce.getUid()).removeValue();
                }

//                final Lead lead = leadList.get(position);
//                leadsAdapter.removeItem(position);
//                new deleteLead(lead).execute();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView_menu);
    }

    //Helper Method
    private void loadParcels()
    {
        ProgressBar progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        Log.e("MYPLACES", "3");
//.orderByChild("uid").equalTo(user.getUid())
        FirebaseRecyclerOptions<Parcel> options = new FirebaseRecyclerOptions.Builder<Parcel>()
                .setQuery(parcel, Parcel.class).build();
        adapterParcel = new FirebaseRecyclerAdapter<Parcel, MenuViewHolder>(options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myplace_item, parent, false);
                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull final Parcel parcel)
            {
                TextView textViewFrom = holder.itemView.findViewById(R.id.from);
                TextView textViewTo = holder.itemView.findViewById(R.id.to);
                TextView textViewDate = holder.itemView.findViewById(R.id.date);
                CardView cardView = holder.itemView.findViewById(R.id.cardview);
                Log.e("MYPLACES", " status "+ parcel.getStatus());

                switch (parcel.getStatus()){

                    case "Accept":
                        cardView.setBackgroundColor(Color.GREEN);
                        break;
                    case "Decline":
                        cardView.setBackgroundColor(Color.RED);
                        break;
                    case "Counter Offer":
                        cardView.setBackgroundColor(Color.YELLOW);
                        break;
                    case "In Progress":
                        cardView.setBackgroundColor(Color.BLUE);
                        break;

                }

//                textViewName.setText(shipper.getName() + " \t"+shipper.getSurname());
//                textViewPhone.setText(shipper.getPhone());
                textViewFrom.setText("From : "+parcel.getPickup());
                textViewTo.setText("To : "+parcel.getDestination());
                textViewDate.setText("Date : "+parcel.getTimestamp());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {

                        switch (parcel.getStatus()){

                            case "Accept":
                               Intent intent = new Intent(MyPlaces.this, TestingMap.class);
                                intent.putExtra("user",user);
                                intent.putExtra("parcel",parcel);
                               startActivity(intent);
                                break;
                            case "Counter Offer":
                                showUpdateDialog(parcel);
                                break;

                        }
                    }
                });
            }
        };

        Log.e("MYPLACES", "4");

        recyclerView_menu.setAdapter(adapterParcel);
    }
    private MaterialSpinner parcelStatus;

    private void showUpdateDialog(final Parcel parcel) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Parcel Options");
        alertDialog.setMessage("Please choose status");

        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.accept_parcel_dialog, null);
        alertDialog.setView(view);

        parcelStatus = view.findViewById(R.id.parcel_status);

        parcelStatus.setItems("Accept", "Decline");
        parcelStatus.setSelectedIndex(Integer.parseInt(parcel.getStatus()));

        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String option = parcelStatus.getItems().get(i).toString();
                {
                    if (option.equals("Accept")) {

                        changeStatus(parcel.getUid(), option);
                        startSendingGPSCoordinates(parcel.getName(), parcel.getUid(), user.getUid());
                    }
                    if (option.equals("Decline")) {

                        changeStatus(parcel.getUid(), option);

                    }

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

    private void changeStatus(String key, String option) {
        parcel.child(key).child("status").setValue(option);
    }

    private void startSendingGPSCoordinates(String name, String parcel_uid, String user_uid) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String dateTime = sdf.format(Calendar.getInstance().getTime());
        Locations loc = new Locations(name, dateTime, parcel_uid, user_uid, Common.GPS_LAT, Common.GPS_LONG, " In Progress");
        LocationDB locationDB = new LocationDB(MyPlaces.this);
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

    private void loadCustomers()
    {
        ProgressBar progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        Log.e("MYPLACES", "3");

        FirebaseRecyclerOptions<Notify> options = new FirebaseRecyclerOptions.Builder<Notify>()
                .setQuery(customer.orderByChild("uid").equalTo(user.getUid()), Notify.class).build();
        adapterCustomer = new FirebaseRecyclerAdapter<Notify, MenuViewHolder>(options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myplace_item, parent, false);
                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull final Notify shipper)
            {
                Log.e("shipper", "shipper "+ shipper);
                TextView textViewFrom = holder.itemView.findViewById(R.id.from);
                TextView textViewTo = holder.itemView.findViewById(R.id.to);
                TextView textViewDate = holder.itemView.findViewById(R.id.date);
                Log.e("MYPLACES", "5");

//                textViewName.setText(shipper.getName() + " \t"+shipper.getSurname());
//                textViewPhone.setText(shipper.getPhone());
                textViewFrom.setText("From : "+shipper.getFrom());
                textViewTo.setText("To : "+shipper.getDestination());
                textViewDate.setText("Date : "+shipper.getDate());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {
                        //We need to send CategoryId to the next sub-activity
//                        Intent intentFood = new Intent(HomeActivity.this, FoodActivity.class);
//                        intentFood.putExtra("categoryId", adapter.getRef(position).getKey());
//                        startActivity(intentFood);
                    }
                });
            }
        };

        Log.e("MYPLACES", "4");

        recyclerView_menu.setAdapter(adapterCustomer);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(adapterParcel != null) {
            adapterParcel.startListening();
        }
        if(adapterCustomer != null) {
            adapterCustomer.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(adapterParcel != null) {
            adapterParcel.stopListening();
        }
        if(adapterCustomer != null) {
            adapterCustomer.stopListening();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (FirebaseDatabase.getInstance() != null)
        {
            FirebaseDatabase.getInstance().goOnline();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(FirebaseDatabase.getInstance()!=null)
        {
            FirebaseDatabase.getInstance().goOffline();
        }
    }
}
