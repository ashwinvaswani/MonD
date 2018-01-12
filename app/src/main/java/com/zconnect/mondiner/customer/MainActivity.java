package com.zconnect.mondiner.customer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView textViewName;
    private IntentIntegrator qrScan;

    private Button btn;

    boolean status = false;
    private Button jj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewName = (TextView) findViewById(R.id.textViewName);

        qrScan = new IntentIntegrator(this);

        btn = (Button) findViewById(R.id.fragment_change);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }
        };

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(!status){
                    LeftFragment f1 = new LeftFragment();
                    fragmentTransaction.add(R.id.FragmentContainer, f1);
                    fragmentTransaction.commit();
                    btn.setText("Load Second fragment");
                    status = true;
                }
                else{
                    RightFragment f2 = new RightFragment();
                    fragmentTransaction.add(R.id.FragmentContainer, f2);
                    fragmentTransaction.commit();
                    btn.setText("Load First fragment");
                    status = false;
                }

            }
        });

        checkUserExist();

    }
    private void checkUserExist() {
        if(mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent tomain = new Intent(MainActivity.this, Register_Activity.class);
                        tomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(tomain);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    textViewName.setText(obj.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    Toast.makeText(this, "Wrong QR Code!", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        qrScan.initiateScan();
    }
}



