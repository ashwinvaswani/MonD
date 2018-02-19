package com.zconnect.mondiner.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register_Activity extends AppCompatActivity {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSUpBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        mNameField = findViewById(R.id.nameField);
        mEmailField = findViewById(R.id.emailField);
        mPasswordField = findViewById(R.id.passwordField);
        mSUpBtn = findViewById(R.id.supbtn);

        mSUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startRegister();

            }
        });
    }

    private void startRegister() {

        final String name = mNameField.getText().toString().trim();
        final String email = mEmailField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Signing Up...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.v("TAg","In On complete");

                    if(task.isSuccessful()){

                        Log.v("TAg","In On complete success");
                        String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference  current_user_db = mDatabase.child(userId);
                        current_user_db.child("Username").setValue(name);
                        current_user_db.child("Email").setValue(email);
                        //current_user_db.child("Image").setValue("default");

                        mProgress.dismiss();
                        Toast.makeText(Register_Activity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                        Intent tomain = new Intent(Register_Activity.this, SetupAcitivty.class);
                        startActivity(tomain);

                    }
                    else if(task.getException().toString().equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")){
                        mProgress.dismiss();
                        Toast.makeText(Register_Activity.this, "The email address is already in use", Toast.LENGTH_SHORT).show();
                    }
                    else if(task.getException().toString().equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.")){
                        mProgress.dismiss();
                        Toast.makeText(Register_Activity.this, "Enter a valid e-mail address", Toast.LENGTH_SHORT).show();
                    }
                    else if(task.getException().toString().equals("com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]")){
                        mProgress.dismiss();
                        Toast.makeText(Register_Activity.this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mProgress.dismiss();
                        Log.e("RegisterActivity","Error : "+ task.getException().toString());
                        Toast.makeText(Register_Activity.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else{
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
        }
    }
}
