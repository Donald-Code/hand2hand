package tshabalala.bongani.courierservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import tshabalala.bongani.courierservice.fragments.BankingDialogFragment;
import tshabalala.bongani.courierservice.helper.MenuViewHolder;
import tshabalala.bongani.courierservice.model.Notify;
import tshabalala.bongani.courierservice.model.Parcel;
import tshabalala.bongani.courierservice.model.Shipper;
import tshabalala.bongani.courierservice.model.User;

public class ParcelActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference parcel;
    FirebaseRecyclerAdapter<Notify, MenuViewHolder> adapterCustomer;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView_menu;
    User user;
   // Shipper shipper;
    Notify notify;
    Button btnSend;

    private EditText etParcelname, etParceldescription,etParcelCategory, etParcelWeight, etPhone, etPickup, etDestination, etPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel);

        user = (User) getIntent().getSerializableExtra("user");
        notify = (Notify) getIntent().getSerializableExtra("notify");

        firebaseDatabase = FirebaseDatabase.getInstance();
        parcel = firebaseDatabase.getReference("Parcel");

        etParcelname = findViewById(R.id.editParcelName);
        etParceldescription = findViewById(R.id.editParcelDescription);
        etParcelCategory = findViewById(R.id.editParcelCategory);
        etParcelWeight = findViewById(R.id.editWeight);
        etPhone = findViewById(R.id.editPhone);
        etPhone.setText(user.getPhone());
        etPickup = findViewById(R.id.editPickupPlace);
        etDestination = findViewById(R.id.editParcelDestination);
        etDestination.setText(notify.getDestination());
        etPrice = findViewById(R.id.editPrice);

        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!etParcelname.getText().toString().equals("")){

                    if(!etParceldescription.getText().toString().equals("")){
                        if(!etPickup.getText().toString().equals("")){
                            if(!etDestination.getText().toString().equals("")){
                                if(!etPhone.getText().toString().equals("")){
                                    if(!etPrice.getText().toString().equals("")){

                                        String name = etParcelname.getText().toString();
                                        String desc = etParceldescription.getText().toString();
                                        String pickup = etPickup.getText().toString();
                                        String destination = etDestination.getText().toString();
                                        String phone = etPhone.getText().toString();

                                        String category = etParcelCategory.getText().toString();
                                        if(TextUtils.isEmpty(category)){
                                           category = "Undefined";
                                        }
                                        double weight = Double.parseDouble(etParcelWeight.getText().toString());
                                        if(TextUtils.isEmpty(etParcelWeight.getText().toString())){
                                            weight =0.0;
                                        }
                                        double amount = Double.parseDouble(etPrice.getText().toString());
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                        String dateTime = sdf.format(Calendar.getInstance().getTime());
                                        Parcel parcels = new Parcel(user.getUid(), notify.getUid(), name, phone, desc,category, weight, pickup,destination, dateTime, amount,"New" );

                                        parcel.child(String.valueOf(System.currentTimeMillis())).setValue(parcels).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                  BankingDialogFragment logOptionsDialogFragment = BankingDialogFragment.getInstance(user);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(logOptionsDialogFragment,"newFragment");
                    ft.commit();


                                                }
                                            }
                                        });




                                    }else{
                                        Toast.makeText(ParcelActivity.this,"Amount cant be empty",Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    Toast.makeText(ParcelActivity.this,"Phone number cant be empty",Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                Toast.makeText(ParcelActivity.this,"Drop of destination cant be empty",Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(ParcelActivity.this,"Pick up locationk cant be empty",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(ParcelActivity.this,"Parcel description cant be empty",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ParcelActivity.this,"Parcel name cant be empty",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
