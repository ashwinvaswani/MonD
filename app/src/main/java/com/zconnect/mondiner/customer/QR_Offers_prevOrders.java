package com.zconnect.mondiner.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QR_Offers_prevOrders extends AppCompatActivity {

    private TextView mTextMessage;
    private IntentIntegrator qrScan;

    private DatabaseReference mRefRestID;

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
        setContentView(R.layout.bottom_nav);
        qrScan = new IntentIntegrator(this);
        mRefRestID = FirebaseDatabase.getInstance().getReference().child("restaurantID");
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "QR error", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Log.e("QrActivity","QR Scanned = " + result.getContents());
                String information[] = result.getContents().split(" ");
                String RestID = information[0].trim();
                Log.e("QrActivity","Table : " + information[1].trim());
                checkRestId(RestID, information[1].trim());

                //info 0 RestID and info 1 has TableID
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkRestId(final String restID, final String tableID) {
        mRefRestID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    if (childSnapShot.getKey().equalsIgnoreCase(restID)) {
                        Log.e("QrActivity","Rest ID found" + restID);
                        checkTableID(childSnapShot, tableID, restID);
                    }
                    else{
                        Toast.makeText(QR_Offers_prevOrders.this, "Restaurant ID not found", Toast.LENGTH_LONG).show();;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("QrActivity","checkRestID : Database connection failed" + databaseError.toString());
            }
        });
    }

    private void checkTableID(DataSnapshot childSnapShot, String tableID, String restID) {
        for(DataSnapshot grandChildSnapShot : childSnapShot.child("table").getChildren()){
            if(grandChildSnapShot.getKey().equalsIgnoreCase(tableID)){
                if(grandChildSnapShot.child("availability").getValue(Integer.class)!=null){
                        if(grandChildSnapShot.child("availability").getValue(Integer.class) == 1 ||
                                grandChildSnapShot.child("availability").getValue(String.class).equals("1") ) {
                            Log.e("QrActivity","Table available. NEW INTENT OPEN! COngo");
                            Intent tomain = new Intent(QR_Offers_prevOrders.this, Tabbed_Menu.class);
                            tomain.putExtra("rest_id", restID);
                            tomain.putExtra("table_id", tableID);
                            startActivity(tomain);
                        }
                        else{
                            Log.e("QrActivity","Table not available");
                            Toast.makeText(this, "Table not available", Toast.LENGTH_SHORT).show();
                        }
                }
                else{
                    Log.e("QrActivity","Table ID WRONG");
                    Toast.makeText(this, "Table not registered!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent tomain = new Intent(QR_Offers_prevOrders.this, Tabbed_Menu.class);
        tomain.putExtra("rest_id", "BBQ_Goa");
        tomain.putExtra("table_id", "tableBBQ1");
        startActivity(tomain);
    }
}

