package com.zconnect.mondiner.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EmailRegisterActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSUpBtn;


    private DatabaseReference mDatabase;

    private ImageButton mSetupImgBtn;
    private EditText mNameField;
    private EditText mContact;
    private Button mSubmitBtn;

    private static final int GALLERY_REQUEST = 1;

    private Uri resultUri = null;

    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;

    private StorageReference mStorageImage;

    private ProgressDialog mProgress;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private String nick;
    private String contact;
    private final int RC_PERM_REQ_EXT_STORAGE = 7;
    private final int MY_GALLERY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_register_);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        mName = findViewById(R.id.nameField);
        mEmailField = findViewById(R.id.emailField);
        mPasswordField = findViewById(R.id.passwordField);
        mSUpBtn = findViewById(R.id.supbtn);

        mSUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSetupAccount();

            }
        });
        mSetupImgBtn = (ImageButton) findViewById(R.id.setupImgBtn);
        mNameField = (EditText) findViewById(R.id.setupNameField);
        mContact = findViewById(R.id.contact_no);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        mStorageImage = FirebaseStorage.getInstance().getReference().child("profile_images");

        mSetupImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (checkPermissionREAD_EXTERNAL_STORAGE(GoogleSetupAcitivty.this)) {
                if (ContextCompat.checkSelfPermission(
                        EmailRegisterActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            EmailRegisterActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                }
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

                //}
            }

        });

    }

    private void startSetupAccount() {

        final String name = mName.getText().toString().trim();
        final String email = mEmailField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();
        nick = mNameField.getText().toString().trim();
        contact = mContact.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(contact)) {

            mProgress.setMessage("Signing Up...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.v("TAg", "In On complete");

                    if (task.isSuccessful()) {

                        Log.v("TAg", "In On complete success");
                        final String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserDatabase = mDatabase.child(userId);
                        final Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("Username", name);
                        taskMap.put("Email", email);

                        if(resultUri!=null) {
                            StorageReference filepath = mStorageImage.child(resultUri.getLastPathSegment());
                            filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.w("onSuccess", "Entered");

                                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                                    taskMap.put("image", downloadUri);
                                    mProgress.dismiss();

                                }
                            });
                        }else{
                            taskMap.put("image", "");
                        }
                        taskMap.put("nick", nick);
                        taskMap.put("contact", contact);
                        currentUserDatabase.updateChildren(taskMap);
                        mProgress.dismiss();
                        Toast.makeText(EmailRegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        Intent tomain = new Intent(EmailRegisterActivity.this, LoginActivity.class);
                        tomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(tomain);

                    } else if (task.getException().toString().equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")) {
                        mProgress.dismiss();
                        Toast.makeText(EmailRegisterActivity.this, "The email address is already in use", Toast.LENGTH_SHORT).show();
                    } else if (task.getException().toString().equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.")) {
                        mProgress.dismiss();
                        Toast.makeText(EmailRegisterActivity.this, "Enter a valid e-mail address", Toast.LENGTH_SHORT).show();
                    } else if (task.getException().toString().equals("com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]")) {
                        mProgress.dismiss();
                        Toast.makeText(EmailRegisterActivity.this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    } else {
                        mProgress.dismiss();
                        Log.e("RegisterActivity", "Error : " + task.getException().toString());
                        Toast.makeText(EmailRegisterActivity.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {


                try {
                    resultUri = result.getUri();
                    mSetupImgBtn.setImageURI(resultUri);
                    if (resultUri == null) {
                        Log.e("Setup Activity", "onActivityResult: got empty imageUri");
                        return;
                    }
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
                    Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                    String path = MediaStore.Images.Media.insertImage(EmailRegisterActivity.this.getContentResolver(), bitmap2, resultUri.getLastPathSegment(), null);
                    resultUri = Uri.parse(path);
                    mSetupImgBtn.setImageURI(resultUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
