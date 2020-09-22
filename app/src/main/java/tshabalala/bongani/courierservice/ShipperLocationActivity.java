package tshabalala.bongani.courierservice;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.model.Shipper;
import tshabalala.bongani.courierservice.model.User;

public class ShipperLocationActivity extends AppCompatActivity {

    EditText etFrom, etTo, etDate;
    Button btnProcess;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference notify;
    User user;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipper_sent);

        user = (User) getIntent().getSerializableExtra("user");

        firebaseDatabase = FirebaseDatabase.getInstance();
        notify = firebaseDatabase.getReference("Notify");

        etFrom = findViewById(R.id.editFrom);
        etTo = findViewById(R.id.editTo);
        etDate = findViewById(R.id.date);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        etDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ShipperLocationActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnProcess = findViewById(R.id.btnSubmit);

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String from = etFrom.getText().toString();
                String to = etTo.getText().toString();
                String timeStamp = etDate.getText().toString();

                if(!TextUtils.isEmpty(from)){

                    if(!TextUtils.isEmpty(to)){

                        if(Common.isInternetAvailable(ShipperLocationActivity.this)){

                            if(!TextUtils.isEmpty(timeStamp)) {

                                Shipper shipper = new Shipper(user.getUid(), user.getName(), user.getSurname(), user.getPhone(), from, to, timeStamp);
                                notify.child(String.valueOf(System.currentTimeMillis())).setValue(shipper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Intent intent = new Intent(ShipperLocationActivity.this, MainActivity.class);
                                            intent.putExtra("user", user);
                                            startActivity(intent);
                                            ShipperLocationActivity.this.finish();
                                        }else{
                                            Toast.makeText(ShipperLocationActivity.this," Could not set Destination place - please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(ShipperLocationActivity.this,"Select date",Toast.LENGTH_SHORT).show();

                            }

                        }else{
                            Toast.makeText(ShipperLocationActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();

                        }

                    }else{
                        Toast.makeText(ShipperLocationActivity.this,"Destination - cant be empty",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ShipperLocationActivity.this,"Place you from - cant be empty",Toast.LENGTH_SHORT).show();
                }

            }
        });

//        if (!Places.isInitialized()) {
//            Places.initialize(getApplicationContext(), "AIzaSyA40vaKhT-TtN1ST6poPZ_KmPIK81MgwdA");
//        }
//
//// Initialize the AutocompleteSupportFragment.
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_from);
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
//                autocompleteFragment.setText(place.getName());
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//
//            }
//        });
//
//        //next one
//        AutocompleteSupportFragment autocompleteTo = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_to);
//
//        autocompleteTo.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//        autocompleteTo.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
//                autocompleteTo.setText(place.getName());
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//
//            }
//        });


    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        etDate.setText(sdf.format(myCalendar.getTime()));
    }
}
