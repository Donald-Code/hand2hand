package tshabalala.bongani.courierservice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import tshabalala.bongani.courierservice.helper.MenuViewHolder;
import tshabalala.bongani.courierservice.helper.MyDividerItemDecoration;
import tshabalala.bongani.courierservice.helper.SwipeToDeleteCallback;
import tshabalala.bongani.courierservice.interfaces.ItemClickListener;
import tshabalala.bongani.courierservice.model.Locations;
import tshabalala.bongani.courierservice.model.Shipper;
import tshabalala.bongani.courierservice.model.User;

import static android.widget.LinearLayout.VERTICAL;

public class CustomerActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference customer;
    FirebaseRecyclerAdapter<User, MenuViewHolder> adapterCustomer;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        firebaseDatabase = FirebaseDatabase.getInstance();
        customer = firebaseDatabase.getReference("Customer");

        swipeRefreshLayout = findViewById(R.id.swipeHome);
        recyclerView_menu = findViewById(R.id.recycler_menu);

        //Load Menu
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        recyclerView_menu.setLayoutManager(mLayoutManager);
        recyclerView_menu.setItemAnimator(new DefaultItemAnimator());
        recyclerView_menu.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
       // loadCustomers();
        enableSwipeToDeleteAndUndo();

    }

    private void deleteCustomer(String key) {
        customer.child(key).removeValue();
    }

    @Override
    public void onStart() {
        super.onStart();

        if(adapterCustomer != null)
        adapterCustomer.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapterCustomer != null)
        adapterCustomer.stopListening();
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

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final User user = adapterCustomer.getItem(position);
                deleteCustomer(user.getUid());
//                new deleteLead(lead).execute();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView_menu);
    }

    //Helper Method
    private void loadCustomers()
    {
        ProgressBar progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

//        customer.addValueEventListener(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
//                        Log.e("TAG", "user " + dataSnapshot);
//                        Map<String,Locations> stringLocationsMap = new HashMap<>();
//                        for(DataSnapshot snap : dataSnapshot.getChildren()){
//                            Log.e("TAG", "user " + snap.getValue());
//
//                            stringLocationsMap.put(snap.getKey(), (Locations) snap.getValue());
//                          //  Locations locations = (Locations) snap.getValue();
//
//
//                        }
//                        Log.e("TAG", "user " + stringLocationsMap.get(0).getCreated_date());
////                        if (dataSnapshot.exists()) {
////
////                            Log.e("TAG", "user data " + dataSnapshot);
////                            mUser.setName(String.valueOf(dataSnapshot.child("name").getValue()));
////                            mUser.setSurname(String.valueOf(dataSnapshot.child("surname").getValue()));
////                            mUser.setPhone(String.valueOf(dataSnapshot.child("phone").getValue()));
////                            mUser.setEmail(String.valueOf(dataSnapshot.child("email").getValue()));
////                            mUser.setUid(uid);
////                            mUser.setRole(String.valueOf(dataSnapshot.child("role").getValue()));
////                            mUser.setDateofbirth(String.valueOf(dataSnapshot.child("dateofbirth").getValue()));
////                            mUser.setGender(String.valueOf(dataSnapshot.child("gender").getValue()));
////                            mUser.setAge(String.valueOf(dataSnapshot.child("age").getValue()));
////                            Common.currentUser = mUser;
////                            Intent intent = new Intent(SignIn.this, AdminActivity.class);
////                            intent.putExtra("user",mUser);
////                            startActivity(intent);
////                            Log.e("TAG", "user error " + dataSnapshot);
////                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.e("TAG", "user " + databaseError.getMessage());
//                    }
//                }
 //       );

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(customer, User.class).build();
        adapterCustomer = new FirebaseRecyclerAdapter<User, MenuViewHolder>(options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull final User user)
            {
                TextView textViewName = holder.itemView.findViewById(R.id.name);
                TextView textViewPhone = holder.itemView.findViewById(R.id.phone);
                ImageView img = holder.itemView.findViewById(R.id.imageView2);

                textViewName.setText(user.getName() + " \t"+user.getSurname());
                textViewPhone.setText(user.getPhone());
                Picasso.with(CustomerActivity.this).load(user.getImage()).into(img);

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

        recyclerView_menu.setAdapter(adapterCustomer);
    }
}
