package tshabalala.bongani.courierservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.model.User;


public class CustomerCameraActivity extends AppCompatActivity {


   private ImageView mSelectImage;
    private Uri mImageUri = null;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private static final int GALLERY_REQUEST = 1;
    User user;
    private Button btnPicture;

    private Button btnBack, btnDone;

    String email;
    String name;
    String surname ;
    //String phone;
    String password;
    String role;
    String instanceid;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_fragment);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
      //  sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
      //  editor = sharedPreferences.edit();

        user = (User) getIntent().getSerializableExtra("user");

       // instanceid = sharedPreferences.getString(Common.TOKEN,"");



        mSelectImage = (ImageView) findViewById(R.id.profile);
        btnPicture = findViewById(R.id.picture);
        btnBack = findViewById(R.id.buttonCancel);
        btnDone = findViewById(R.id.buttonDone);

        mProgress = new ProgressDialog(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent intent = new Intent(CustomerCameraActivity.this, ChooseActivity.class);
            intent.putExtra("user",user);
            startActivity(intent);

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAdding();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            // do your stuff
//        } else {
//            signInAnonymously();
//        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("TAG", "signInAnonymously:FAILURE", exception);
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data!= null){
            mImageUri = data.getData();
            mSelectImage.setImageURI(mImageUri);
            mSelectImage.setDrawingCacheEnabled(true);
            mSelectImage.buildDrawingCache();

        }
    }

    private void startAdding() {
        if ( mImageUri != null){

            final StorageReference filepath = mStorage.child("Profile_Images").child(mImageUri.getLastPathSegment());

            mProgress.setTitle("Registering...");
            mProgress.show();

            final UploadTask uploadTask = filepath.putFile(mImageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    mProgress.dismiss();

                    Toast.makeText(CustomerCameraActivity.this, "Please upload a photo", Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();


                                mDatabase = FirebaseDatabase.getInstance().getReference(user.getRole());
                                String uid = String.valueOf(System.currentTimeMillis());
                                Log.e("ROLE", "role " +user.getRole() + " currentuser "+ user.getUid());
                                    final User currentuser = new User(uid,user.getRole(), user.getName(), user.getSurname(), user.getIdnumber(),user.getGender(),user.getDateofbirth(),user.getAge(), user.getEmail(), user.getPassword(), user.getPhone(), downloadUri.toString());
                                    mDatabase.child(uid).setValue(currentuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(CustomerCameraActivity.this, SignIn.class);
                                           // intent.putExtra("user",currentuser);
                                            startActivity(intent);
                                           // CustomerCameraActivity.this.finish();

//                                            mAuth.createUserWithEmailAndPassword(currentuser.getEmail(), currentuser.getPassword())
//                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<AuthResult> task) {
//                                                            if (task.isSuccessful()) {
//
//
//                                                            }
//
//                                                            if (!task.isSuccessful()) {
//                                                                try {
//                                                                    throw task.getException();
//                                                                } catch (FirebaseAuthWeakPasswordException e) {
//                                                                    mDatabase.child(user.getUid()).removeValue();
//                                                                    Toast.makeText(CustomerCameraActivity.this, getString(R.string.login_weak_password), Toast.LENGTH_SHORT).show();
//
//                                                                    //  editPassword.setError(getString(R.string.login_weak_password));
//
//                                                                } catch (Exception e) {
//                                                                    if (e.toString().contains("WEAK_PASSWORD")) {
//                                                                        // editPassword.setError(getString(R.string.login_weak_password));
//                                                                        mDatabase.child(user.getUid()).removeValue();
//                                                                        Toast.makeText(CustomerCameraActivity.this, getString(R.string.login_weak_password), Toast.LENGTH_SHORT).show();
//
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                    })
//                                                    .addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
//                                                            if (e instanceof FirebaseAuthWeakPasswordException) {
//                                                                // editPassword.setError(getString(R.string.login_weak_password));
//                                                              //  mDatabase.child(user.getUid()).removeValue();
//                                                                Toast.makeText(CustomerCameraActivity.this, getString(R.string.login_weak_password), Toast.LENGTH_SHORT).show();
//
//                                                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                                                                //  editEmail.setError(getString(R.string.login_bad_email));
//                                                              //  mDatabase.child(user.getUid()).removeValue();
//                                                                Toast.makeText(CustomerCameraActivity.this, getString(R.string.login_bad_email), Toast.LENGTH_SHORT).show();
//
//                                                            } else if (e instanceof FirebaseAuthUserCollisionException) {
//                                                                // editEmail.setError(getString(R.string.login_user_exists));
//                                                             //   mDatabase.child(user.getUid()).removeValue();
//                                                                Toast.makeText(CustomerCameraActivity.this, getString(R.string.login_user_exists), Toast.LENGTH_SHORT).show();
//
//                                                            }
//
//                                                        }
//                                                    });

                                          //  Toast.makeText(CameraActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                                        }else{

                                           // mDatabase.child(user.getUid()).removeValue();
                                              Toast.makeText(CustomerCameraActivity.this, "User already exist!", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                                mProgress.dismiss();
                                // Toast.makeText(getActivity(), " Profile picture set!! ", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle failures
                                // ...
                                mProgress.dismiss();

                            }
                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    mProgress.setMessage("Uploaded " + ((int) progress) + " %...");
                }
            });



        }else{
            Toast.makeText(CustomerCameraActivity.this, "Please upload photo ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

    }


}
