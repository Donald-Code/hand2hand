package tshabalala.bongani.courierservice;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import tshabalala.bongani.courierservice.fragments.PersonalDetailDialogFragment;
import tshabalala.bongani.courierservice.fragments.SelectUserDialogFragment;
import tshabalala.bongani.courierservice.model.User;


public class ChooseActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSignUp, btnSignIn;
    TextView textSlogan;
    User user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    // User is signed in
                    Log.w("TAG USER","User "+ user);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                } else {
                    // User is signed out
                }
            }
        };

        user = (User) getIntent().getSerializableExtra("user");
//        if(user != null){
//
//            PersonalDetailDialogFragment newClientDialogFragment = PersonalDetailDialogFragment.newInstance(user.getRole());
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            Fragment previous = getFragmentManager().findFragmentByTag("newEventDialog");
//            if (previous != null){
//                transaction.remove(previous);
//            }
//            transaction.add(newClientDialogFragment, "newEventDialog");
//            transaction.addToBackStack(null);
//            transaction.commit();
//        }


        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);

        textSlogan = findViewById(R.id.txtSlogan);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        textSlogan.setTypeface(typeface);

        btnSignUp.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSignUp)
        {
//            SignUp
            addUser();
//            Intent intent = new Intent(ChooseActivity.this, SignUp.class);
//            startActivity(intent);
        }
        else if(v.getId() == R.id.btnSignIn)
        {
//            Login
            Intent intent = new Intent(ChooseActivity.this, SignIn.class);
            startActivity(intent);

        }
    }

    private void addUser(){
        SelectUserDialogFragment newClientDialogFragment = SelectUserDialogFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment previous = getFragmentManager().findFragmentByTag("newEventDialog");
        if (previous != null){
            transaction.remove(previous);
        }
        transaction.add(newClientDialogFragment, "newEventDialog");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}