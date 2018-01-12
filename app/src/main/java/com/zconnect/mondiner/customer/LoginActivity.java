package com.zconnect.mondiner.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private SignInButton mGoogleBtn;
    private Button newAccountbtn;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private static final String TAG = "Login_Activity";
    private ProgressDialog mProgress;



    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignInBtn;
    private DatabaseReference mDatabaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);


        mGoogleBtn = (SignInButton) findViewById(R.id.GoogleBtn);

        mProgress = new ProgressDialog(this);

        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);
        mSignInBtn = (Button) findViewById(R.id.SignIn);

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this,"ERROR Signing in!", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        newAccountbtn = (Button) findViewById(R.id.newAccount);

        newAccountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, Register_Activity.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);
            }
        });


        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void checkLogin() {

        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Checking Login...");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                   if(task.isSuccessful()){
                       mProgress.dismiss();
                       checkUserExist();
                   }
                   else{
                       mProgress.dismiss();
                       Toast.makeText(LoginActivity.this, "Error Signing In through EMail",Toast.LENGTH_LONG).show();
                   }
                }
            });
        }

    }

    private void checkUserExist() {
        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){
                    Intent tomain = new Intent(LoginActivity.this, MainActivity.class);
                    tomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(tomain);
                } else{
                    Toast.makeText(LoginActivity.this, "You need to setup your account!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mProgress.setMessage("Starting Sign In...");
            mProgress.show();
            if(result.isSuccess()){

                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
                mProgress.dismiss();
                Log.e(LoginActivity.class.getSimpleName(), result.getStatus().toString());
                Toast.makeText(LoginActivity.this, "Failed!", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete" + task.isSuccessful());

                        if(!task.isSuccessful()){
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            mProgress.dismiss();
                            setupUserDataAndFinish(mAuth.getCurrentUser());
                            Intent setupIntent = new Intent(LoginActivity.this, SetupAcitivty.class);
                            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(setupIntent);
                        }
                    }
                });


    }

    private void CheckUserExist() {
        if(mAuth.getCurrentUser() != null){
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(user_id)){
                        Intent setupIntent = new Intent(LoginActivity.this, MainActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }
                    else{
                        Intent setupIntent = new Intent(LoginActivity.this, SetupAcitivty.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void setupUserDataAndFinish(@NonNull final FirebaseUser user) {
        Uri photoUri = user.getPhotoUrl();
        String photoUrl;
        String defaultPhotoUrl = "https://firebasestorage.googleapis.com/v0/b/zconnect-89fbd.appspot.com/o/PhonebookImage%2FdefaultprofilePhone.png?alt=media&token=5f814762-16dc-4dfb-ba7d-bcff0de7a336";
        if (photoUri != null) photoUrl = photoUri.toString();
        else photoUrl = defaultPhotoUrl;
        DatabaseReference currentUserDbRef = mDatabaseUsers.child(user.getUid());
        currentUserDbRef.child("Image").setValue(photoUrl);
        currentUserDbRef.child("Username").setValue(user.getDisplayName());
        currentUserDbRef.child("Email").setValue(user.getEmail());
        finish(); /*Make Sure HomeActivity exists*/
    }
    //@Override
    //protected void onStart() {
      //  super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
    //}
}


