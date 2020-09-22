package tshabalala.bongani.courierservice;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import tshabalala.bongani.courierservice.fragments.PersonalCustomerDetailDialogFragment;
import tshabalala.bongani.courierservice.fragments.PersonalDetailDialogFragment;
import tshabalala.bongani.courierservice.fragments.SelectUserDialogFragment;
import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.model.User;

import static tshabalala.bongani.courierservice.helper.Common.CLIENT;
import static tshabalala.bongani.courierservice.helper.Common.USER_EMAIL;
import static tshabalala.bongani.courierservice.helper.Common.USER_NAME;
import static tshabalala.bongani.courierservice.helper.Common.USER_PASSWORD;
import static tshabalala.bongani.courierservice.helper.Common.USER_PHONE;


public class SignIn extends AppCompatActivity {

    EditText editEmail, editPassord;
    Button btnSignIn;
  //  com.rey.material.widget.CheckBox rememberMe;
    private FirebaseAuth mAuth;
    TextView mTextViewForgot;
    RadioButton radioCustomer;
    RadioButton radioShipper;
    RadioButton radioAdmin;
    TextView register;

    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReferenceAdmin;
    private DatabaseReference mDatabaseReferenceCustomer;
    private DatabaseReference mDatabaseReferenceShipper;
    String ref = "Shipper";
    public final static int LOCATION_PERMISSION_REQUEST = 100;

    @Override
    protected void onStart(){
        super.onStart();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   Log.e("OKAY","adad");
                }
                break;

        }
    }


    //Helper Methods
    private void requestRunTimePermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, LOCATION_PERMISSION_REQUEST);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        Paper.init(this);
        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReferenceAdmin = firebaseDatabase.getReference("Administrator");
        mDatabaseReferenceCustomer = firebaseDatabase.getReference("Customer");
        mDatabaseReferenceShipper = firebaseDatabase.getReference("Shipper");
//        String role = sharedPreferences.getString(Common.ROLE,"");
//        if(role.equals("Admin")) {
//            startActivity(new Intent(SignIn.this, AdminActivity.class));
//        }else{
//            startActivity(new Intent(SignIn.this, MainActivity.class));
//        }

        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestRunTimePermission();
        }

        radioAdmin = findViewById(R.id.radioAdmin);
        radioCustomer = findViewById(R.id.radioCustomer);
        radioShipper = findViewById(R.id.radioShipper);
        register = findViewById(R.id.register_textview);

        editEmail = findViewById(R.id.editEmail);
        editPassord = findViewById(R.id.editPassord);
        btnSignIn = findViewById(R.id.btnSignIn);
       // rememberMe = findViewById(R.id.remember_me);
        mTextViewForgot  = findViewById(R.id.login_textview_forgot_pass);



        // If user forgot his password, send a default e-mail with reset instructions.
        mTextViewForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPassordDialog();

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioAdmin.isChecked()){
                    startActivity(new Intent(SignIn.this, RegisterAdminActivity.class));
                }else if(radioCustomer.isChecked()){
                    Intent intent = new Intent(SignIn.this, PersonalCustomerDetailDialogFragment.class);
                    intent.putExtra("role", "Customer");
                    startActivity(intent);
//                    PersonalCustomerDetailDialogFragment newClientDialogFragment = PersonalCustomerDetailDialogFragment.newInstance("Customer");
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                    Fragment previous = getFragmentManager().findFragmentByTag("newEventDialog");
//                    if (previous != null){
//                        transaction.remove(previous);
//                    }
//                    transaction.add(newClientDialogFragment, "newEventDialog");
//                    transaction.addToBackStack(null);
//                    transaction.commit();

                }else if(radioShipper.isChecked()){

                    Intent intent = new Intent(SignIn.this, PersonalDetailDialogFragment.class);
                             intent.putExtra("role", "Shipper");
                               startActivity(intent);
//                    PersonalDetailDialogFragment newClientDialogFragment = PersonalDetailDialogFragment.newInstance("Shipper");
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                    Fragment previous = getFragmentManager().findFragmentByTag("newEventDialog");
//                    if (previous != null){
//                        transaction.remove(previous);
//                    }
//                    transaction.add(newClientDialogFragment, "newEventDialog");
//                    transaction.addToBackStack(null);
//                    transaction.commit();
                }

            }
        });

    }

    private void loginUser() {

        String mEmail = editEmail.getText().toString().trim();
        String mPass = editPassord.getText().toString().trim();
        if(TextUtils.isEmpty(mEmail)){
            Toast.makeText(this,getString(R.string.login_hint_email), Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(mPass)){
            Toast.makeText(this,getString(R.string.login_hint_password),Toast.LENGTH_LONG).show();
            return;
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(SignIn.this);
        mProgressDialog.setMessage(getString(R.string.login_progressbar_login));
        mProgressDialog.show();
        if(radioAdmin.isChecked()){
            mDatabaseReferenceAdmin.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            assert user != null;
                            Log.e("TAG", " sauu inside " + user);
                            if (user.getPassword().equals(mPass)) {
                                mProgressDialog.dismiss();
                                Common.currentUser = user;
                                Intent intent = new Intent(SignIn.this, AdminActivity.class);
                                intent.putExtra("user", user);
                                startActivity(intent);

                                break;
                            } else {
                                // Toast.makeText(SignIn.this,"Please check your email/ password",Toast.LENGTH_LONG).show();
                            }
                        }
                    }catch(Exception e){
                        mProgressDialog.dismiss();
                        Toast.makeText(SignIn.this,"Please check your email/ password",Toast.LENGTH_LONG).show();
                    }
//

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    mProgressDialog.dismiss();
                    Toast.makeText(SignIn.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            mProgressDialog.show();

        }else if(radioCustomer.isChecked()){
            mDatabaseReferenceCustomer.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            assert user != null;
                            Log.e("TAG", " sauu inside " + user);
                            if (user.getPassword().equals(mPass)) {
                                mProgressDialog.dismiss();
                                Common.currentUser = user;
                                Intent intent = new Intent(SignIn.this, MainActivity.class);
                                intent.putExtra("user", user);
                                startActivity(intent);

                                finish();
                                break;

                            } else {
                                // Toast.makeText(SignIn.this,"Please check your email/ password",Toast.LENGTH_LONG).show();
                            }
                        }
                    }catch (Exception e){
                        mProgressDialog.dismiss();
                        Toast.makeText(SignIn.this,"Please check your email/ password",Toast.LENGTH_LONG).show();
                    }
//

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    mProgressDialog.dismiss();
                    Toast.makeText(SignIn.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            mProgressDialog.show();
        }else if(radioShipper.isChecked()){
            mDatabaseReferenceShipper.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try{
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        Log.e("TAG", " sauu inside " + user);
                        if (user.getPassword().equals(mPass)) {
                            mProgressDialog.dismiss();
                            Common.currentUser = user;
                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
finish();
                            break;
                        } else {
                            // Toast.makeText(SignIn.this,"Please check your email/ password",Toast.LENGTH_LONG).show();
                        }
                    }

                }catch(Exception e){
                        mProgressDialog.dismiss();
                        Toast.makeText(SignIn.this,"Please check your email/ password",Toast.LENGTH_LONG).show();
                }

//

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    mProgressDialog.dismiss();
                    Toast.makeText(SignIn.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            mProgressDialog.show();
        }
//        else
//
//            mDatabaseReference.child("Shipper").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    //Checking User avail
//
//                    //Get User data
//                    Log.e("TAG", " sauu "+ dataSnapshot);
//                    mProgressDialog.dismiss();
//
//                    Log.e("TAG", " sauu "+ dataSnapshot);
//                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//
//                        User user = snapshot.getValue(User.class);
//                        assert user != null;
//                        Log.e("TAG", mPass+ " sauu inside "+ user.getPassword());
//                        if (user.getPassword().equals(mPass))
//                        {
//                            Common.currentUser = user;
//                            Intent intent = new Intent(SignIn.this, MainActivity.class);
//                            intent.putExtra("user",user);
//                            startActivity(intent);
//
//
//                        }
//                    }
////
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    Toast.makeText(SignIn.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            mDatabaseReference.child("Customer").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    //Checking User avail
//
//                    //Get User data
//                    Log.e("TAG", " sauu "+ dataSnapshot);
//                    mProgressDialog.dismiss();
//
//                    Log.e("TAG", " sauu "+ dataSnapshot);
//                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//
//                        User user = snapshot.getValue(User.class);
//                        assert user != null;
//                        Log.e("TAG", " sauu inside "+ user);
//                        if (user.getPassword().equals(mPass))
//                        {
//                            Common.currentUser = user;
//                            Intent intent = new Intent(SignIn.this, User.class);
//                            intent.putExtra("user",user);
//                            startActivity(intent);
//
//
//                        }
//                    }
////
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    Toast.makeText(SignIn.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }



    }

    /**
     * Shows alert dialog with EditText to enter
     * an email.
     */
    private void showForgotPassordDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.forget_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(SignIn.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputEmail = view.findViewById(R.id.email);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", (dialogBox, id) -> {

                })
                .setNegativeButton("cancel",
                        (dialogBox, id) -> dialogBox.cancel());

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Show toast message when no text is entered
            if (TextUtils.isEmpty(inputEmail.getText().toString())) {
                Toast.makeText(SignIn.this, "error email cant be empty", Toast.LENGTH_SHORT).show();
                return;
            } else {
                alertDialog.dismiss();
            }
            mAuth.sendPasswordResetEmail(inputEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // Show Toast based on whether password reset email was sent or not
                    if(task.isSuccessful()) {
                        Toast toast = Toast.makeText(SignIn.this, getString(R.string.login_forgot_pass_toast), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(SignIn.this, "login_forgot_pass_toast_unsucc", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

        });
    }

    private void userLogin() {

        String mEmail = editEmail.getText().toString().trim();
        String mPass = editPassord.getText().toString().trim();
        if(TextUtils.isEmpty(mEmail)){
            Toast.makeText(this,getString(R.string.login_hint_email), Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(mPass)){
            Toast.makeText(this,getString(R.string.login_hint_password),Toast.LENGTH_LONG).show();
            return;
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(SignIn.this);
        mProgressDialog.setMessage(getString(R.string.login_progressbar_login));
        mProgressDialog.show();
        //Signing the user in
        mAuth.signInWithEmailAndPassword(mEmail, mPass)
                .addOnCompleteListener(SignIn.this, task -> {
                    mProgressDialog.dismiss();
                    if(task.isSuccessful()) {

                        User mUser = new User();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String uid = user.getUid();
                        if(radioAdmin.isChecked()){

                            mDatabaseReferenceAdmin.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.e("TAG", "user " + dataSnapshot);
                                            if (dataSnapshot.exists()) {

                                                Log.e("TAG", "user data " + dataSnapshot);
                                            mUser.setName(String.valueOf(dataSnapshot.child("name").getValue()));
                                                mUser.setSurname(String.valueOf(dataSnapshot.child("surname").getValue()));
                                            mUser.setPhone(String.valueOf(dataSnapshot.child("phone").getValue()));
                                            mUser.setEmail(String.valueOf(dataSnapshot.child("email").getValue()));
                                            mUser.setUid(uid);
                                            mUser.setRole(String.valueOf(dataSnapshot.child("role").getValue()));
                                            mUser.setDateofbirth(String.valueOf(dataSnapshot.child("dateofbirth").getValue()));
                                            mUser.setGender(String.valueOf(dataSnapshot.child("gender").getValue()));
                                            mUser.setAge(String.valueOf(dataSnapshot.child("age").getValue()));
                                           Common.currentUser = mUser;
                                                Intent intent = new Intent(SignIn.this, AdminActivity.class);
                                                intent.putExtra("user",mUser);
                                                startActivity(intent);
                                                Log.e("TAG", "user error " + dataSnapshot);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e("TAG", "user " + databaseError.getMessage());
                                        }
                                    }
                            );

                        }else {
                            mDatabaseReferenceShipper.child(mAuth.getUid()).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.e("TAG", "user " + dataSnapshot);
                                            if (dataSnapshot.exists()) {

                                                Log.e("TAG", "user data " + dataSnapshot);
                                            mUser.setName(String.valueOf(dataSnapshot.child("name").getValue()));
                                            mUser.setSurname(String.valueOf(dataSnapshot.child("surname").getValue()));
                                            mUser.setPhone(String.valueOf(dataSnapshot.child("phone").getValue()));
                                            mUser.setEmail(String.valueOf(dataSnapshot.child("email").getValue()));
                                            mUser.setUid(uid);
                                            mUser.setRole(String.valueOf(dataSnapshot.child("role").getValue()));
                                                mUser.setDateofbirth(String.valueOf(dataSnapshot.child("dateofbirth").getValue()));
                                                mUser.setGender(String.valueOf(dataSnapshot.child("gender").getValue()));
                                                mUser.setAge(String.valueOf(dataSnapshot.child("age").getValue()));
                                           Common.currentUser = mUser;
                                                Log.e("TAG", "user error " + dataSnapshot);
                                                Intent intent = new Intent(SignIn.this, MainActivity.class);
                                                intent.putExtra("user",mUser);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e("TAG", "Not a shipper " );
                                        }
                                    }
                            );
                            mDatabaseReferenceCustomer.child(mAuth.getUid()).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.e("TAG", "user " + dataSnapshot);

                                            if (dataSnapshot.exists()) {
                                                Log.e("TAG", "user data " + dataSnapshot);
                                            mUser.setName(String.valueOf(dataSnapshot.child("name").getValue()));
                                                mUser.setSurname(String.valueOf(dataSnapshot.child("surname").getValue()));
                                            mUser.setPhone(String.valueOf(dataSnapshot.child("phone").getValue()));
                                            mUser.setEmail(String.valueOf(dataSnapshot.child("email").getValue()));
                                            mUser.setUid(uid);
                                            mUser.setRole(String.valueOf(dataSnapshot.child("role").getValue()));
                                                mUser.setDateofbirth(String.valueOf(dataSnapshot.child("dateofbirth").getValue()));
                                                mUser.setGender(String.valueOf(dataSnapshot.child("gender").getValue()));
                                                mUser.setAge(String.valueOf(dataSnapshot.child("age").getValue()));

                                            Common.currentUser = mUser;
                                                Log.e("TAG", "user error " + dataSnapshot);

                                                Intent intent = new Intent(SignIn.this, MainActivity.class);
                                                intent.putExtra("user",mUser);
                                                startActivity(intent);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e("TAG", "Not a customer ");
                                        }
                                    }
                            );
                        }

                    }else{
                        Toast.makeText(this,"Please check your email/ password",Toast.LENGTH_LONG).show();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

