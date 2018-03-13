package com.zconnect.mondiner.customer;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

public class SetupAcitivty extends AppCompatActivity {

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
    private String userID;
    private final int RC_PERM_REQ_EXT_STORAGE = 7;
    private final int MY_GALLERY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_acitivty);
        //TODO : Remove hard coded userID    *IMPORTANT*
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mSetupImgBtn = (ImageButton) findViewById(R.id.setupImgBtn);
        mNameField = (EditText) findViewById(R.id.setupNameField);
        mContact = findViewById(R.id.contact_no);
        mSubmitBtn = (Button) findViewById(R.id.submitButton);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        mStorageImage = FirebaseStorage.getInstance().getReference().child("profile_images");

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSetupAccount();

            }
        });

        mSetupImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (checkPermissionREAD_EXTERNAL_STORAGE(SetupAcitivty.this)) {
                if (ContextCompat.checkSelfPermission(
                        SetupAcitivty.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            SetupAcitivty.this,
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

        nick = mNameField.getText().toString().trim();
        contact = mContact.getText().toString().trim();
        //final String user_id = mAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(nick) && resultUri != null && !TextUtils.isEmpty(contact)) {

            mProgress.setMessage("Finishing Setup...");
            mProgress.show();

            StorageReference filepath = mStorageImage.child(resultUri.getLastPathSegment());
            filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.w("onSuccess", "Entered");

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    mDatabaseUsers.child(userID).child("nick").setValue(nick);
                    mDatabaseUsers.child(userID).child("image").setValue(downloadUri);
                    mDatabaseUsers.child(userID).child("contact").setValue(contact);
                    mProgress.dismiss();
                    Intent tomain = new Intent(SetupAcitivty.this, TabbedMenu.class);
                    tomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(tomain);

                }
            });

        }
        else{
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
                    String path = MediaStore.Images.Media.insertImage(SetupAcitivty.this.getContentResolver(), bitmap2, resultUri.getLastPathSegment(), null);
                    resultUri = Uri.parse(path);
                    mSetupImgBtn.setImageURI(resultUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
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
}

