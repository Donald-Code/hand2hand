package tshabalala.bongani.courierservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import tshabalala.bongani.courierservice.helper.Common;
import tshabalala.bongani.courierservice.model.User;


public class ProfileActivity extends AppCompatActivity {


    private ImageView imageEdit;
    private EditText mSurname;
    private EditText mIDNumber;
    private ImageView mSelectImage;
    private Uri mImageUri = null;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private static final int GALLERY_REQUEST = 1;

    private TextView mName;
    private TextView mPhone;
    private TextView mMail;
    private TextView mDob;
    private TextView mGender;
    private TextView mAge;

    User user;

    Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        user = (User) getIntent().getSerializableExtra("user");

        mDatabase = FirebaseDatabase.getInstance().getReference(user.getRole());
        mName = (TextView) findViewById(R.id.name_text_view);
        mPhone = (TextView)findViewById(R.id.telephone_text_view);
        mMail = (TextView) findViewById(R.id.mail_text_view);

        mDob = (TextView) findViewById(R.id.dob_text_view);
        mGender = (TextView)findViewById(R.id.gender_text_view);
        mAge = (TextView) findViewById(R.id.age_text_view);

        btnDone = findViewById(R.id.buttonDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });


//        mSelectImage = (ImageView) findViewById(R.id.profile);
//        imageEdit = (ImageView) findViewById(R.id.edit);
//        mProgress = new ProgressDialog(this);
//        mStorage = FirebaseStorage.getInstance().getReference();
//        mSelectImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent, GALLERY_REQUEST);
//            }
//        });

        setTexts(user.getName(),user.getPhone(),user.getEmail(),user.getDateofbirth(),user.getGender(),user.getAge());

//        Picasso.with(ProfileActivity.this).load(user.getImage()).into(mSelectImage);
//        mName.setText(user.getName()+"\t"+user.getSurname());
//        mSurname.setText(user.getEmail());
//        mIDNumber.setText(user.getPhone());


    }

    public void setTexts(String name, String phone, String mail, String dob, String gender, String age) {
        mName.setText(name);
        mPhone.setText(phone);
        mMail.setText(mail);

        mDob.setText(dob);
        mGender.setText(gender);
        mAge.setText(age);
    }

    private void startAdding() {
        if ( mImageUri != null){
            mProgress.setTitle("Updating profile picture...");
            mProgress.show();
            final StorageReference filepath = mStorage.child(Common.currentUser.getPhone()).child("Profile_Images").child(mImageUri.getLastPathSegment());

            final UploadTask uploadTask = filepath.putFile(mImageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
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
                                mDatabase.child(Common.currentUser.getPhone()).child("image").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                                        }
                                    }
                                });

                                mProgress.dismiss();
                                Toast.makeText(getApplicationContext(), " Profile picture set!! ", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle failures
                                // ...
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
            Toast.makeText(getApplicationContext(), "Failed... ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data!= null){
            mImageUri = data.getData();
            mSelectImage.setImageURI(mImageUri);
            mSelectImage.setDrawingCacheEnabled(true);
            mSelectImage.buildDrawingCache();

            startAdding();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

    }


}
