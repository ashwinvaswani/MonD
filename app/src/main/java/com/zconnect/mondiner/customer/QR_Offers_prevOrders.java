package com.zconnect.mondiner.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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
import com.zconnect.mondiner.customer.utils.Details;

public class QR_Offers_prevOrders extends AppCompatActivity {

    private TextView mTextMessage;
    private IntentIntegrator qrScan;

    private DatabaseReference mUserName;
    private FirebaseAuth mAuth;
    private DatabaseReference mRefRestID;
    private SharedPreferences preferences;
    private SharedPreferences preferencesQRData;
    private ValueEventListener restListener;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_offers:
                    mTextMessage.setText("Offers");
                    return true;
                case R.id.navigation_scanQR: {
                    qrScan.initiateScan();
                    return true;
                }
                case R.id.navigation_previousOrders:
                    mTextMessage.setText("Previous Orders");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_offers_prevorders);
        qrScan = new IntentIntegrator(this);
        mRefRestID = FirebaseDatabase.getInstance().getReference().child("restaurants");
        mTextMessage = (TextView) findViewById(R.id.message);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = preferences.getString("userID", "");
        String username = preferences.getString("username","");
        Details.USER_ID = userID;
        Details.USERNAME = username;
        Log.e("QR_Offers_prevOrders --","User ID is : "+Details.USER_ID + "Username is : "+ Details.USERNAME);
        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userid = preferences.getString("UserID","");
        if(!userid.equalsIgnoreCase("")){
            userid = userid+"Ishaan";
        }

        Log.e("QR_Offers_prevOrders","This is the userID obtained : " + userid);
*//*
        final View offers = findViewById(R.id.navigation_offers);
        offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offers.setBackgroundColor(Color.BLUE);
            }
        });*/
        mUserName = FirebaseDatabase.getInstance().getReference().child("Users").child(Details.USER_ID).child("username");
/*        mUserName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString()!=null) {
                    Details.USERNAME = dataSnapshot.getValue().toString();
                    Log.e("QR_Offers_prevOrders","This is the username obtained : " + Details.USERNAME);
                }
                else
                    Log.e("QR_Offers_prevOrders","datasnapshot is null");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("QR_Offers_prevOrders","datasnapshot cancelled");
            }
        });*/

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "QR not scanned", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Log.e("QrActivity", "QR Scanned = " + result.getContents());
                String information[] = result.getContents().split(";");
                String RestID = information[0].trim();
                String TableID = information[1].trim();
                preferencesQRData = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferencesQRData.edit();
                editor.putString("restaurantId", RestID);
                editor.putString("currentTableId",TableID);
                editor.apply();
                Log.e("QrActivity", "Table : " + information[1].trim());
                checkRestId(RestID, TableID);

                //info 0 RestID and info 1 has TableID
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void checkRestId(final String restID, final String tableID) {
        restListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    if (childSnapShot.getKey().equals(restID)) {
                        Log.e("QrActivity", "Rest ID found" + restID);
                  //      Toast.makeText(QR_Offers_prevOrders.this, "The RestID was equal!", Toast.LENGTH_SHORT).show();
                        checkTableID(childSnapShot, tableID, restID);
                    } else {
                        continue;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRefRestID.addValueEventListener(restListener);
        /*mRefRestID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    if (childSnapShot.getKey().equals(restID)) {
                        Log.e("QrActivity", "Rest ID found" + restID);
                        Toast.makeText(QR_Offers_prevOrders.this, "The RestID was equal!", Toast.LENGTH_SHORT).show();
                        checkTableID(childSnapShot, tableID, restID);
                    } else {
                        continue;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("QrActivity", "checkRestID : Database connection failed" + databaseError.toString());
            }
        });*/
    }

    private void checkTableID(DataSnapshot childSnapShot, String tableID, String restID) {
        for (DataSnapshot grandChildSnapShot : childSnapShot.child("table").getChildren()) {
            if (grandChildSnapShot.getKey().equals(tableID)) {
        /*        if(grandChildSnapShot.child("availability").getValue(String.class)!=null){
                        if(grandChildSnapShot.child("availability").getValue(String.class).equals("true") ||
                                grandChildSnapShot.child("availability").getValue(String.class).equals("1") ) {
                            Log.e("QrActivity","Table available. NEW INTENT OPEN! COngo");*/
                preferencesQRData = PreferenceManager.getDefaultSharedPreferences(this);
                String restaurantId = preferencesQRData.getString("restaurantId", "");
                String currentTableId = preferencesQRData.getString("currentTableId","");
                Details.REST_ID = restaurantId;
                Details.TABLE_ID = currentTableId;
                Log.e("QRActivity","Checking shared preferences : " + Details.REST_ID + "--" + Details.TABLE_ID);
                mRefRestID.child(Details.REST_ID).child("table").child(Details.TABLE_ID).child("availability").setValue("false");
                Intent tomain = new Intent(QR_Offers_prevOrders.this, Tabbed_Menu.class);
                Toast.makeText(this, "Please select your dishes...", Toast.LENGTH_SHORT).show();
                mRefRestID.removeEventListener(restListener);
                startActivity(tomain);
            } else {
                continue;
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();/*
        Intent tomain = new Intent(QR_Offers_prevOrders.this, Tabbed_Menu.class);
        tomain.putExtra("rest_id", "redChillies");
        tomain.putExtra("table_id", "redChilliesTable_03");
        startActivity(tomain);*/
    }

}
