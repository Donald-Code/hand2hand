package tshabalala.bongani.courierservice.fragments;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import tshabalala.bongani.courierservice.CameraActivity;
import tshabalala.bongani.courierservice.R;
import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.helper.SerializableKey;
import tshabalala.bongani.courierservice.model.User;

import static tshabalala.bongani.courierservice.helper.Common.checkFutureIDValid;
import static tshabalala.bongani.courierservice.helper.Common.isIDValid;


public class PersonalDetailDialogFragment extends AppCompatActivity implements TextWatcher
{


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

//
//    public static PersonalDetailDialogFragment newInstance(String selectionChoose){
//        Bundle args = new Bundle();
//        args.putString(SerializableKey.Role, selectionChoose);
//        PersonalDetailDialogFragment frag = new PersonalDetailDialogFragment();
//        frag.setArguments(args);
//        return frag;
//    }

    @Override
    public void onStart()
    {
        super.onStart();
//        Dialog dialog = getDialog();
//        if (dialog != null)
//        {
//            int width = ViewGroup.LayoutParams.MATCH_PARENT;
//            int height = ViewGroup.LayoutParams.MATCH_PARENT;
//            dialog.getWindow().setLayout(width, height);
//        }
    }

 //   AppInterface mInterface;
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//    }

    TextView textViewMessage;
    @NonNull
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if (savedInstanceState == null){
//            View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_sign_up, null);
//          //  Toolbar toolbar = (Toolbar) dialogView.findViewById(R.id.dialogTitleToolbar);
//            textViewMessage = (TextView) dialogView.findViewById(R.id.dialogMessageTV);
//            Bundle args = getArguments();
          //  role = args.getString(SerializableKey.Role);
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

            role = getIntent().getStringExtra("role");

            Button btnProcess = findViewById(R.id.btnProcess);
            btnProcess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptRegistration();
                }
            });

//            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
//                    .setView(dialogView)
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                           // goBackToSelectionFragment();
//                            dialog.dismiss();
//                        }
//                    })
//                    .setCancelable(false)
//                    .setPositiveButton("Next", null)
//                    .create();
//            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                @Override
//                public void onShow(final DialogInterface dialog) {
//                    Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
//                    b.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                          //  Log.e("Show"," Gender "+ Common.getGender(editIDNumber.getText().toString()) + " age "+ Common.getAge(editIDNumber.getText().toString())+ " DoB "+ Common.getDateOfBirth(editIDNumber.getText().toString()));
//
//                            attemptRegistration();
//                        }
//                    });
//                }
//            });
//            alertDialog.setCancelable(false);
//            alertDialog.setCanceledOnTouchOutside(false);
//            return alertDialog;
        }
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



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            assert focusView != null;
            focusView.requestFocus();
        } else {



            String gender = Common.getGender(idnumber);
            String dob = Common.getDateOfBirth(idnumber);
            String age = String.valueOf(Common.getAge(idnumber));
            Intent intent = new Intent(this, CameraActivity.class);
            intent.putExtra("user", new User(role, name, surname, idnumber,gender,dob,age, email, password, phone));
            startActivity(intent);


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

    private void goBackToSelectionFragment() {

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


}
