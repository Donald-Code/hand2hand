package tshabalala.bongani.courierservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import tshabalala.bongani.courierservice.model.User;

public class ProfileEditActivity extends AppCompatActivity {

    User user;
    private EditText mNameEdit;
    private EditText mPhoneEdit;
    private EditText mMailEdit;
    private EditText mDobEdit;
    private EditText mGenderEdit;

    private Button mAcceptButton;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_edit);


        user = (User) getIntent().getSerializableExtra("user");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(user.getRole());

        mNameEdit = (EditText) findViewById(R.id.profileedit_name_edit_text);
        mPhoneEdit = (EditText) findViewById(R.id.profileedit_telephone_edit_text);
        mMailEdit = (EditText) findViewById(R.id.profileedit_mail_edit_text);

        mDobEdit = (EditText) findViewById(R.id.profileedit_dob_edit_text);
        mGenderEdit = (EditText) findViewById(R.id.profileedit_gender_edit_text);
        mAcceptButton = (Button) findViewById(R.id.profileedit_accept_button);

        setTexts(user.getName(),user.getPhone(),user.getEmail(),user.getDateofbirth(),user.getGender(),user.getAge());

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setListenerForAccept(mNameEdit.getText().toString(),
                        mPhoneEdit.getText().toString(),
                        mMailEdit.getText().toString());
            }
        });

        mNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkIfEditTextValid(getString(R.string.profile_error_name), mNameEdit.getText().toString(), 1);
            }
        });
        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkIfEditTextValid(getString(R.string.profile_error_phone), mPhoneEdit.getText().toString(), 2);
            }
        });
        mMailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkIfEditTextValid(getString(R.string.profile_error_mail), mMailEdit.getText().toString(), 3);
            }
        });
    }

    public void setTexts(String name, String phone, String mail, String dob, String gender, String age) {
        mNameEdit.setText(name);
        mPhoneEdit.setText(phone);
        mMailEdit.setText(mail);
        mDobEdit.setText(dob);
        mGenderEdit.setText(gender);

    }

    public void setListenerForAccept(String name, String phone, String mail) {
        if (phone.length() < 9 ||
                name.length() <= 1 ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(
                        mail).matches())  {
            sendToastRequestToView(1);
        } else {
            updateUserDb();
        }
    }

    public void makeToast(int numberOfMessage) {
        String text;
        if(numberOfMessage == 1) {
            text = getString(R.string.profile_error_submit);
        } else {
            text = getString(R.string.profile_toast_update);
            Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);

        }
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void sendToastRequestToView(int dataNumber) {
        makeToast(dataNumber);
    }

    public void checkIfEditTextValid(String errorMessage, String text, int dataNumber) {
        if(dataNumber == 1) {
            if(text.length() <= 1) {
                setEditTextErrors(errorMessage, dataNumber);
            } else {
                // Send new to db
                setUserDetail(dataNumber, text);
            }
        } else if(dataNumber == 2) {
            if(text.length() < 9) {
               setEditTextErrors(errorMessage, dataNumber);
            } else {
                // Send new to db
                setUserDetail(dataNumber, text);
            }
        } else if(dataNumber == 3) {
            if(!isValidEmailAddress(text)) {
                setEditTextErrors(errorMessage, dataNumber);
            } else {
                // Send new to db
                setUserDetail(dataNumber, text);
            }
        }
    }

    public void setUserDetail(int dataNumber, String value) {
        if(dataNumber == 3) {
            user.setEmail(value);
        } else if(dataNumber == 2) {
            user.setPhone(value);
        } else if(dataNumber == 1) {
            user.setName(value);
        }
    }

    public static boolean isValidEmailAddress(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    /**
     * Receives a call from editPresenter upon user's button click.
     * Updates user data in db.
     */
    public void updateUserDb() {
        final String userUid = user.getUid();
        mDatabaseReference.child(userUid).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //DATA ALREADY EXISTS
                        String oldMail = String.valueOf(dataSnapshot.child("email").getValue());
                        if(!oldMail.equals(user.getEmail())) {
                            resetUserAuthMail(user.getEmail());
                        }
                        mDatabaseReference.child(userUid).setValue(user);
                        sendToastRequestToView(2);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("warning", "oncancelled");
                    }
                });
    }

    public void setEditTextErrors(String errorMessage, int dataNumber) {
        if(dataNumber == 1) {
            mNameEdit.setError(errorMessage);
        } else if(dataNumber == 2) {
            mPhoneEdit.setError(errorMessage);
        } else if(dataNumber == 3) {
            mMailEdit.setError(errorMessage);
        }
    }

    private void resetUserAuthMail(String newmail) {
        FirebaseUser loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        loggedUser.updateEmail(newmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("MAILRESET", "Succesful");
                        }
                    }
                });
    }



}
