package com.zconnect.mondiner.customer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;


public class UnusedMainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private IntentIntegrator qrScan;

    private Button btn;

    boolean status = false;
    private Button jj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        qrScan = new IntentIntegrator(this);

        btn = (Button) findViewById(R.id.fragment_change);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    Intent loginIntent = new Intent(UnusedMainActivity.this, LoginActivity.class);
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
        //qrScan.initiateScan();
    }
    private void checkUserExist() {
        if(mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent tomain = new Intent(UnusedMainActivity.this, Register_Activity.class);
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
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
       // qrScan.initiateScan();
        Intent bottom_nav = new Intent(UnusedMainActivity.this, QR_Offers_prevOrders.class);
        bottom_nav.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(bottom_nav);
    }
}



