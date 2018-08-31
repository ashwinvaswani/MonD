package com.zconnect.mondiner.customer;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zconnect.mondiner.customer.utils.Details;

import java.lang.reflect.Field;

public class QR_Offers_prevOrders extends AppCompatActivity {

    private IntentIntegrator qrScan;

    private DatabaseReference mUserName;
    private FirebaseAuth mAuth;
    private DatabaseReference mRefRestID;
    private SharedPreferences preferences;
    private ValueEventListener restListener;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_offers:
                    fragmentTransaction.replace(R.id.fragmentContainer, new OffersFragment()).commit();
                    return true;
                case R.id.navigation_scanQR: {
                    fragmentTransaction.replace(R.id.fragmentContainer, new QRFragment()).commit();
                    return true;
                }
                case R.id.navigation_previousOrders:
                    fragmentTransaction.replace(R.id.fragmentContainer, new PrevOrdersFragment()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_offers_prevorders);
        getOverflowMenu();
        getSupportActionBar().setTitle("");
        // qrScan = new IntentIntegrator(this);
        mAuth = FirebaseAuth.getInstance();
        mRefRestID = FirebaseDatabase.getInstance().getReference().child("restaurants");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = preferences.getString("userID", "");
        String username = preferences.getString("username", "");
        Details.USER_ID = userID;
        Details.USERNAME = username;
        if (checkSelfPermission(android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);
        }
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, new QRFragment()).commit();
        Log.e("QR_Offers_prevOrders --", "User ID is : " + Details.USER_ID + "Username is : " + Details.USERNAME);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "QR not scanned", Toast.LENGTH_LONG).show();
            } else {
                try {//Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    Log.e("QrActivity", "QR Scanned = " + result.getContents());
                    String information[] = result.getContents().split(";");
                    String RestID = information[0].trim();
                    String TableID = information[1].trim();
                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("restaurantId", RestID);
                    editor.putString("currentTableId", TableID);
                    editor.apply();
                    Log.e("QrActivity", "Table : " + information[1].trim());
                    checkRestId(RestID, TableID);
                }
                catch (Exception e){
                    Toast.makeText(this, "Data cannot be processed", Toast.LENGTH_SHORT).show();
                }
                //info 0 RestID and info 1 has TableID
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_qr_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_signout_qr_activity) {
            Intent setupIntent = new Intent(QR_Offers_prevOrders.this, LoginActivity.class);
            //setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("userID");
            editor.remove("username");
            editor.commit();
            Log.e("TabbedMenu", "Shared Preferences Check --- UserID : " + preferences.getString("userID", "")
                    + "username : " + preferences.getString("username", "")
                    + "restID : " + preferences.getString("restaurantId", "")
                    + "tableId : " + preferences.getString("currentTableId", ""));
            Details.USER_ID = "";
            Details.USERNAME = "";
            //Log.e("TabbedMenu","mAuth : "+mAuth.getCurrentUser().getUid());
            mAuth.signOut();
            try {
                mGoogleSignInClient.signOut().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
            } catch (Exception e) {

            }
            //onStart();
            startActivity(setupIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();/*
        Intent tomain = new Intent(QR_Offers_prevOrders.this, TabbedMenu.class);
        tomain.putExtra("rest_id", "redChillies");
        tomain.putExtra("table_id", "redChilliesTable_03");
        startActivity(tomain);*/
    }

}
