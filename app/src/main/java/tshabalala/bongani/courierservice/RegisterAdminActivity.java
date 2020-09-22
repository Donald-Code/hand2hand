package tshabalala.bongani.courierservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.helper.SerializableKey;
import tshabalala.bongani.courierservice.model.User;

import static tshabalala.bongani.courierservice.helper.Common.checkFutureIDValid;
import static tshabalala.bongani.courierservice.helper.Common.isIDValid;

public class RegisterAdminActivity extends AppCompatActivity implements TextWatcher {

    EditText editPhone, editName, editPassword;
    EditText editSurname, editIDNumber, editConfirmPassword, editEmail;
    Button btnSignUp;
    String email;
    String name  ;
    String surname ;
    String idnumber;
    String phone;
    String password;
    String confirm;
    String role;
    String uid;

    boolean cancel = false;
    View focusView = null;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private DatabaseReference mDatabase;
    FirebaseDatabase firebaseDatabase;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

       // firebaseDatabase = FirebaseDatabase.getInstance();//.getReference("Shipper");
      //  mDatabase = firebaseDatabase.getReference("Administrator");
       // mDatabase.keepSynced(true);


        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null) {
                // User is signed in
                Log.w("TAG USER","User "+ user.getDisplayName());
                // startActivity(new Intent(getApplicationContext(), MainActivity.class));

            } else {
                // User is signed out
            }
        };


        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.editPassord);
        editSurname = findViewById(R.id.editSurname);
        editIDNumber = findViewById(R.id.editIDNumber);
        editEmail = findViewById(R.id.editEmail);
        editConfirmPassword = findViewById(R.id.editConfirmPassord);
        editConfirmPassword.addTextChangedListener(this);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });
    }

    private void attemptRegistration() {

        // Store values at the time of the login attempt.
        name = editName.getText().toString();
        surname = editSurname.getText().toString();
        idnumber = editIDNumber.getText().toString();
        email = editEmail.getText().toString();
        password = editPassword.getText().toString();
        phone = editPhone.getText().toString();
        confirm = editConfirmPassword.getText().toString();


        if (TextUtils.isEmpty(name)) {
            editName.setError("Error - name can't be empty");
            focusView = editName;
            cancel = true;

        }

        if (TextUtils.isEmpty(surname)) {
            editSurname.setError("Error - surname can't be empty");
            focusView = editSurname;
            cancel = true;

        }

        if (TextUtils.isEmpty(phone)) {
            editPhone.setError("Error - cellphone can't be empty");
            focusView = editPhone;
            cancel = true;

        }

        if (TextUtils.isEmpty(password)) {
            editPassword.setError("Error - password can't be empty");
            focusView = editPassword;
            cancel = true;

        }

        if (TextUtils.isEmpty(confirm)) {
            editConfirmPassword.setError("Error - confirm password can't be empty");
            focusView = editConfirmPassword;
            cancel = true;

        }

        if (!TextUtils.isEmpty(phone)) {
            if (!Common.validatePhone(phone)) {
                editPhone.setError("Error - Enter correct cellphone");
                focusView = editPhone;
                cancel = true;

            }
        }


        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Error - email can't be empty");
            focusView = editEmail;
            cancel = true;

        }

        if (!TextUtils.isEmpty(email)) {
            if (!Common.isEmailValid(email)) {
                editEmail.setError("Error - Enter correct email");
                focusView = editEmail;
                cancel = true;

            }
        }

        if (!TextUtils.isEmpty(password)) {
            if (!Common.isPasswordValid(password)) {
                editPassword.setError("Error - password should be more than 5 digits");
                focusView = editPassword;
                cancel = true;

            }
        }

        if(!password.equals(editConfirmPassword.getText().toString()))
        {
            editConfirmPassword.setError("Password don't match");
            focusView = editConfirmPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(idnumber)) {
            editIDNumber.setError("Error - ID number can't be empty");
            focusView = editIDNumber;
            cancel = true;

        }

        if (!TextUtils.isEmpty(idnumber)) {
            if (!isIDValid(idnumber)) {
                editIDNumber.setError("Enter correct ID number");
                focusView = editIDNumber;
                cancel = true;

            }
        }

        if (!TextUtils.isEmpty(idnumber)) {
            if (isIDValid(idnumber)) {
                if(!checkFutureIDValid(idnumber)){
                    editIDNumber.setError("Future ID number are not allowed");
                    focusView = editIDNumber;
                    cancel = true;
                }

            }
        }

//
//
//        // create email and password
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()) {
//
//                            uid = mAuth.getCurrentUser().getUid();
//
//                        }
//
//                        if(!task.isSuccessful()) {
//                            try {
//                                throw task.getException();
//                            } catch(FirebaseAuthWeakPasswordException e) {
//                                editPassword.setError(getString(R.string.login_weak_password));
//                                focusView = editPassword;
//                                cancel = true;
//                            } catch(Exception e) {
//                                if(e.toString().contains("WEAK_PASSWORD")) {
//                                    editPassword.setError(getString(R.string.login_weak_password));
//                                    focusView = editPassword;
//                                    cancel = true;
//                                }
//                            }
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        if(e instanceof FirebaseAuthWeakPasswordException) {
//                            editPassword.setError(getString(R.string.login_weak_password));
//                            focusView = editPassword;
//                            cancel = true;
//                        } else if(e instanceof FirebaseAuthInvalidCredentialsException) {
//                            editEmail.setError(getString(R.string.login_bad_email));
//                            focusView = editEmail;
//                            cancel = true;
//                        } else if(e instanceof FirebaseAuthUserCollisionException) {
//                            editEmail.setError(getString(R.string.login_user_exists));
//                            focusView = editEmail;
//                            cancel = true;
//                        }
//
//                    }
//                });


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            assert focusView != null;
            focusView.requestFocus();
        } else {

            String gender = Common.getGender(idnumber);
            String dob = Common.getDateOfBirth(idnumber);
            String age = String.valueOf(Common.getAge(idnumber));

            ProgressDialog mProgress = new ProgressDialog(this);
            mProgress.setTitle("Registering...");
            mProgress.show();

             if(Common.isInternetAvailable(this)) {
            Log.e("Error", " fa");
            mDatabase = FirebaseDatabase.getInstance().getReference("Administrator");
            Log.e("Error", " fa 1");
                 String uid = String.valueOf(System.currentTimeMillis());
            final User currentuser = new User(uid, "Administrator", name, surname, idnumber, gender, dob, age, email, password, phone);
            Log.e("Error", " fa 2");
            mDatabase.child(uid).setValue(currentuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mProgress.dismiss();
                    if (task.isSuccessful()) {

                        Intent intent = new Intent(RegisterAdminActivity.this, AdminActivity.class);
                        intent.putExtra("user", currentuser);
                        startActivity(intent);
                        RegisterAdminActivity.this.finish();
                    }
                }
            });
        }
//                mDatabase.child(String.valueOf(System.currentTimeMillis())).setValue(currentuser).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                          //  mProgress.dismiss();
//
//                            Intent intent = new Intent(RegisterAdminActivity.this, AdminActivity.class);
//                            intent.putExtra("user", currentuser);
//                            startActivity(intent);
//                          //  RegisterAdminActivity.this.finish();
//                        } else {
//                           // mProgress.dismiss();
//                            Log.e("Error", " error");
//                        }
//                    }
//                });
            Log.e("Error", " mo");
//            }else{
//                Toast.makeText(this,"No internet connection",Toast.LENGTH_SHORT).show();
//            }
          //  mProgress.dismiss();


        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        validateConfirmPassword(editConfirmPassword.getText().toString());

    }

    private void validateConfirmPassword(String confirm)
    {
        if(!confirm.equals(editConfirmPassword.getText().toString()))
        {
            editConfirmPassword.setError("Password don't match");
            editConfirmPassword.requestFocus();
        }
    }


}
