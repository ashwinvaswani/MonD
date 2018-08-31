package com.zconnect.mondiner.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.facebook.drawee.backends.pipeline.Fresco;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.mondiner.customer.models.Tabs;
import com.zconnect.mondiner.customer.utils.Details;
import com.zconnect.mondiner.customer.utils.FirebasePersistence;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TabbedMenu extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    //private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int counter;
    private String[] cArray;
    private String[] cIdArray;
    private ElegantNumberButton incDec;
    private ArrayList<Tabs> tabsInfo = new ArrayList<>();
    private TextView noItemText;
    private TabLayout tabLayout;
    private DatabaseReference mMenuRef;
    private ValueEventListener mMenuRefListener;
    private DatabaseReference mRestRef;
    private ValueEventListener mRestRefListener;
    private DatabaseReference mTableRef;
    private SharedPreferences preferences;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabaseUsers;
    private Query mDatabase;
    private ValueEventListener mDatabaseUsersListener;
    private ValueEventListener mCurrentUsersListener;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebasePersistence.getDatabase();
        super.onCreate(savedInstanceState);
        mRestRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("TabbedMenu", "Name of Restaurant : " + dataSnapshot.getValue(String.class));
                setTitle(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        setContentView(R.layout.activity_tabbed__menu);
       /* mDrawerLayout = findViewById(R.id.main_content);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        mAuth = FirebaseAuth.getInstance();
        Fresco.initialize(this);

        /*if(!isConnected(TabbedMenu.this) || internetConnectionAvailable(1000))
            buildDialog(TabbedMenu.this).show();
        else {
            Toast.makeText(TabbedMenu.this,"Welcome", Toast.LENGTH_SHORT).show();

        }*/
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String restaurantId = preferences.getString("restaurantId", "");
        String currentTableId = preferences.getString("currentTableId", "");
        String userID = preferences.getString("userID", "");
        String username = preferences.getString("username", "");
        Details.REST_ID = restaurantId;
        Details.TABLE_ID = currentTableId;
        Details.USER_ID = userID;
        Details.USERNAME = username;
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading");
        mProgress.show();
        mProgress.setCancelable(false);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mRestRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("info").child("name");
        //incDec = findViewById(R.id.inc_dec);
        //Details.REST_ID = rest_id;
        //Details.TABLE_ID = table_id;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        noItemText = findViewById(R.id.no_item_text);
        tabLayout = findViewById(R.id.tabs);

        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(" Red Chillies");


        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        //mMenuRef = FirebasePersistence.getInstance().getReference().child("restaurants").child("redChillies");

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID).child("currentOrder").child("activeUsers");

       /* findViewById(R.id.action_prev_orders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tomain = new Intent(TabbedMenu.this, PreviousOrdersActivity.class);
                startActivity(tomain);
            }
        });*/

        mMenuRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //         Log.e("TabbedMenu:", "in onDataChange" + dataSnapshot.child("info")
                //               .child("servesCuisine").getValue(String.class));
                //String cuisine = dataSnapshot.child("info").child("servesCuisine").getValue(String.class);
                tabsInfo.clear();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Tabs tab = new Tabs();
                    tab.setCatID(childSnapshot.getKey());
                    tab.setCatName(childSnapshot.child("title").getValue(String.class));
                    tab.setCatPriority(childSnapshot.child("priorityNos").getValue(String.class));
                    tabsInfo.add(tab);

                }
                setupViewPager();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TabbedMenu", "DatabaseError onCancelled" + databaseError.toString());
            }
        };


        //fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent tomain = new Intent(TabbedMenu.this, CartActivity.class);
                tomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(tomain);
            }
        });
    }

    private void setupViewPager() {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        TabFragment tabs[] = new TabFragment[tabsInfo.size()];
        for (int i = 0; i < tabsInfo.size(); i++) {
//            Log.e("TabbedMenu -- ", "cArray[i] : " + cArray[i] + "cIdArray : " + cIdArray[i] + " Current Cuisines : " + Details.CUISINE_ID_ARRAY.toString());
            tabs[i] = TabFragment.getInstance(tabsInfo.get(i));
            adapter.addFrag(tabs[i], tabsInfo.get(i).getCatName());
        }
        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tabbed__menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_scan_qr) {
            Intent setupIntent = new Intent(TabbedMenu.this, QR_Offers_prevOrders.class);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("restaurantId");
            editor.remove("currentTableId");
            editor.commit();
            Log.e("TabbedMenu", "Shared Preferences Check --- UserID : " + preferences.getString("userID", "")
                    + "username : " + preferences.getString("username", "")
                    + "restID : " + preferences.getString("restaurantId", "")
                    + "tableId : " + preferences.getString("currentTableId", ""));
            Details.REST_ID = "";
            Details.TABLE_ID = "";
            //onStart();
            startActivity(setupIntent);
            return true;
        } else if (id == R.id.action_offers) {
            Intent offerIntent = new Intent(TabbedMenu.this, OffersActivity.class);
            startActivity(offerIntent);
        }else if(id==R.id.action_credits) {
            Intent offerIntent = new Intent(TabbedMenu.this, CreditsActivity.class);
            startActivity(offerIntent);
        } else if (id == R.id.action_signout) {
            Intent setupIntent = new Intent(TabbedMenu.this, LoginActivity.class);
            //setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("userID");
            editor.remove("username");
            editor.remove("restaurantId");
            editor.remove("currentTableId");
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
        }else if(id == R.id.action_prev_orders){
            Intent offerIntent = new Intent(TabbedMenu.this, PreviousOrdersActivity.class);
            startActivity(offerIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            //Log.e("TabbedMenu", "no of frags " + mFragmentList.size());
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            mProgress.hide();
//            noItemText.setText("No" + title + "items to show");
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.e("TabbedMenu","UID : "+mAuth.getCurrentUser().getUid());
        mAuth = FirebaseAuth.getInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.e("TabbedMenu", "Shared Preferences Check --- UserID : " + preferences.getString("userID", "")
                + "username : " + preferences.getString("username", "")
                + "restID : " + preferences.getString("restaurantId", "")
                + "tableId : " + preferences.getString("currentTableId", ""));
        final String restaurantId = preferences.getString("restaurantId", "");
        final String currentTableId = preferences.getString("currentTableId", "");
        String userID = preferences.getString("userID", "");
        String username = preferences.getString("username", "");
        try{
            if(preferences.getString("confirmationActivity","").equalsIgnoreCase("true")){
                Intent tomain = new Intent(TabbedMenu.this, ConfirmationActivity.class);
                startActivity(tomain);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        /*Intent tomain = new Intent(TabbedMenu.this, QR_Offers_prevOrders.class);
        startActivity(tomain);*/
        mDatabaseUsersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!mAuth.getCurrentUser().getUid().equals(null)) {
                    int flag = 0;
                    for (DataSnapshot users : dataSnapshot.getChildren()) {
                        if (users.getKey().equals(mAuth.getCurrentUser().getUid())) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        Log.e("TabbedMenu", "In flag==0. Intent should open");
                        //IMPORTANT
                        //Here only GoogleSetup is there because the email and password data is added to the database while registering.
                        //And the data of google sign-in is added at the time of setup in GoogleSetupActivity
                        Intent toSetup = new Intent(TabbedMenu.this, GoogleSetupAcitivty.class);
                        startActivity(toSetup);
                    } else if (restaurantId.equals(null) || restaurantId.isEmpty() || currentTableId.equals(null) || currentTableId.isEmpty()) {
                        Intent tomain = new Intent(TabbedMenu.this, QR_Offers_prevOrders.class);
                        startActivity(tomain);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };



        if (mAuth.getCurrentUser() == null) {
            Intent tomain = new Intent(TabbedMenu.this, LoginActivity.class);
            tomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(tomain);
        } else {
            try {
                mDatabaseUsers.addValueEventListener(mDatabaseUsersListener);
            } catch (Exception e) {
                Log.e("TabbedMenu", "Error : " + e);
            }
        }
        //TODO : Change this to query

        Log.e("TabbedMenu", "The details are : " + restaurantId + " " + currentTableId + " " + userID + " " + username);


        mMenuRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("menu").child("categories");
        mMenuRef.addValueEventListener(mMenuRefListener);
        mRestRef.addValueEventListener(mRestRefListener);
        if (Details.USER_ID != null && !preferences.getString("confirmationActivity","").equalsIgnoreCase("true")) {

            mTableRef.child(Details.USER_ID).child("name").setValue(Details.USERNAME);
            mTableRef.child(Details.USER_ID).child("confirmStatus").setValue("no");

        }

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMenuRef.removeEventListener(mMenuRefListener);
        mDatabaseUsers.removeEventListener(mDatabaseUsersListener);
        mRestRef.removeEventListener(mRestRefListener);
    }

    @Override
    protected void onResume() {
        super.onResume();/*
        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID).child("currentOrder").child("activeUsers").child(Details.USER_ID);
        mTableRef.child("name").setValue(Details.USERNAME);
        mTableRef.child("confirmStatus").setValue("no");*/

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mMenuRef.addValueEventListener(mMenuRefListener);
        mRestRef.addValueEventListener(mRestRefListener);
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
        else return false;
        } else
        return false;
    }

    private boolean internetConnectionAvailable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress!=null && !inetAddress.equals("");
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }

}
