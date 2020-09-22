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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import tshabalala.bongani.courierservice.helper.MenuViewHolder;
import tshabalala.bongani.courierservice.helper.MyDividerItemDecoration;
import tshabalala.bongani.courierservice.helper.SwipeToDeleteCallback;
import tshabalala.bongani.courierservice.interfaces.ItemClickListener;
import tshabalala.bongani.courierservice.model.Shipper;
import tshabalala.bongani.courierservice.model.User;

import static android.widget.LinearLayout.VERTICAL;

public class ShipperActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference shipper;
    FirebaseRecyclerAdapter<User, MenuViewHolder> adapterShipper;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shippers);

        firebaseDatabase = FirebaseDatabase.getInstance();
        shipper = firebaseDatabase.getReference("Shipper");

        swipeRefreshLayout = findViewById(R.id.swipeHome);
        recyclerView_menu = findViewById(R.id.recycler_menu);

        //Load Menu
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        recyclerView_menu.setLayoutManager(mLayoutManager);
        recyclerView_menu.setItemAnimator(new DefaultItemAnimator());
        recyclerView_menu.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));

        enableSwipeToDeleteAndUndo();
        Log.e("TAG","1");
        loadShipper();
        Log.e("TAG","2");

    }

    private void deleteUser(String key) {
        shipper.child(key).removeValue();
    }

    /**
     * Deleting a post
     * when swiping LEFT OR swiping RIGHT.
     */
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final User user = adapterShipper.getItem(position);
                deleteUser(user.getUid());
//                leadsAdapter.removeItem(position);
//                new deleteLead(lead).execute();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView_menu);
    }
    @Override
    public void onStart() {
        super.onStart();

        adapterShipper.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        adapterShipper.stopListening();
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

    //Helper Method
    private void loadShipper()
    {
        ProgressBar progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        Log.e("TAG","3");
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(shipper, User.class).build();
        adapterShipper = new FirebaseRecyclerAdapter<User, MenuViewHolder>(options) {
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
                Log.e("TAG","5");
                TextView textViewName = holder.itemView.findViewById(R.id.name);
                TextView textViewPhone = holder.itemView.findViewById(R.id.phone);
                ImageView img = holder.itemView.findViewById(R.id.imageView2);

                textViewName.setText(user.getName() + " \t"+user.getSurname());
                textViewPhone.setText(user.getPhone());
                Picasso.with(ShipperActivity.this).load(user.getImage()).into(img);
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
        Log.e("TAG","4");

        recyclerView_menu.setAdapter(adapterShipper);
    }
}
