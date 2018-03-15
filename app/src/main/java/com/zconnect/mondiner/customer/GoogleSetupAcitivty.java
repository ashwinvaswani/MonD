package com.zconnect.mondiner.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GoogleSetupAcitivty extends AppCompatActivity {

    private SimpleDraweeView mGoogleSetupImage;
    private EditText mNameField;
    private EditText mEmailField;
    private EditText mContact;
    private EditText mNick;
    private String userID;
    private Button mSubmitBtn;

    private static final int GALLERY_REQUEST = 1;

    private Uri resultUri = null;

    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;

    private StorageReference mStorageImage;

    private ProgressDialog mProgress;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;


    private SharedPreferences preferences;
    private final int RC_PERM_REQ_EXT_STORAGE = 7;
    private final int MY_GALLERY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_setup_acitivty);
        Fresco.initialize(this);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mGoogleSetupImage = findViewById(R.id.google_setup_image);
        mNameField = findViewById(R.id.google_name);
        mEmailField = findViewById(R.id.google_email);
        mContact = findViewById(R.id.google_contact_number);
        mNick = findViewById(R.id.google_setup_name);
        Log.e("GoogleSetup", "in onCreate of Activity");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String googleName = preferences.getString("username", "");
        final String googleEmail = preferences.getString("userEmail", "");
        final String googleImage = preferences.getString("userImage", "");
        mNameField.setText(googleName);
        mEmailField.setText(googleEmail);
        mGoogleSetupImage.setImageURI(googleImage);
        resultUri = Uri.parse(googleImage);

        if (ContextCompat.checkSelfPermission(
                GoogleSetupAcitivty.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    GoogleSetupAcitivty.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    12345
            );
        }

        mSubmitBtn = findViewById(R.id.google_submit);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mProgress = new ProgressDialog(this);

        mStorageImage = FirebaseStorage.getInstance().getReference().child("profile_images");

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSetupAccount();

            }
        });

        mGoogleSetupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (checkPermissionREAD_EXTERNAL_STORAGE(GoogleSetupAcitivty.this)) {
                if (ContextCompat.checkSelfPermission(
                        GoogleSetupAcitivty.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            GoogleSetupAcitivty.this,
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

        final String name = mNameField.getText().toString().trim();
        final String email = mEmailField.getText().toString().trim();
        final String nick = mNick.getText().toString().trim();
        final String contact = mContact.getText().toString().trim();
        if (resultUri != null && !TextUtils.isEmpty(contact) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)) {

            mProgress.setMessage("Finishing Setup...");
            mProgress.show();
            if (!resultUri.toString().equals(preferences.getString("userImage", ""))) {
                StorageReference filepath = mStorageImage.child(resultUri.getLastPathSegment());
                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.w("onSuccess", "Entered");
                        Log.v("TAg", "In On complete success");
                        final String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserDatabase = mDatabaseUsers.child(userId);
                        final Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("Username", name);
                        taskMap.put("Email", email);
                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                        taskMap.put("image", downloadUri);
                        taskMap.put("nick", nick);
                        taskMap.put("contact", contact);
                        currentUserDatabase.updateChildren(taskMap);
                        mProgress.dismiss();
                        Intent tomain = new Intent(GoogleSetupAcitivty.this, TabbedMenu.class);
                        startActivity(tomain);

                    }
                });
            }else{
                final String userId = mAuth.getCurrentUser().getUid();
                DatabaseReference currentUserDatabase = mDatabaseUsers.child(userId);
                final Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put("Username", name);
                taskMap.put("Email", email);
                taskMap.put("image", resultUri.toString());
                taskMap.put("nick", nick);
                taskMap.put("contact", contact);
                try {
                    currentUserDatabase.updateChildren(taskMap);
                }catch (Exception e){
                    e.printStackTrace();
                }
                mProgress.dismiss();
                Intent tomain = new Intent(GoogleSetupAcitivty.this, TabbedMenu.class);
                startActivity(tomain);
            }

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
                    mGoogleSetupImage.setImageURI(resultUri);
                    if (resultUri == null) {
                        Log.e("Setup Activity", "onActivityResult: got empty imageUri");
                        return;
                    }
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
                    Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                    String path = MediaStore.Images.Media.insertImage(GoogleSetupAcitivty.this.getContentResolver(), bitmap2, resultUri.getLastPathSegment(), null);
                    resultUri = Uri.parse(path);
                    mGoogleSetupImage.setImageURI(resultUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

/*    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            mGoogleSetupImage.setImageBitmap(result);
            Log.e("GoogleSetup", "Image from Bitmap");

        }
    }*/
}

