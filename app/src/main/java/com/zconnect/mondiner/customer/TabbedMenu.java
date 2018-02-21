package com.zconnect.mondiner.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.mondiner.customer.models.Tabs;
import com.zconnect.mondiner.customer.utils.Details;
import com.zconnect.mondiner.customer.utils.FirebasePersistence;

import java.util.ArrayList;
import java.util.List;

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
    private DatabaseReference mDatabaseUsers;
    private ValueEventListener mDatabaseUsersListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebasePersistence.getDatabase();
        super.onCreate(savedInstanceState);
        mRestRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("TabbedMenu","Name of Restaurant : "+dataSnapshot.getValue(String.class));
                setTitle(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mAuth = FirebaseAuth.getInstance();
        Fresco.initialize(this);
        setContentView(R.layout.activity_tabbed__menu);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String restaurantId = preferences.getString("restaurantId", "");
        String currentTableId = preferences.getString("currentTableId","");
        String userID = preferences.getString("userID", "");
        String username = preferences.getString("username","");
        Details.REST_ID = restaurantId;
        Details.TABLE_ID = currentTableId;
        Details.USER_ID = userID;
        Details.USERNAME = username;
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading");
        mProgress.show();
        mProgress.setCancelable(false);
        mRestRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("info").child("name");
        mRestRef.addValueEventListener(mRestRefListener);
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
        getSupportActionBar().setTitle(" Red Chillies");


        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        //mMenuRef = FirebasePersistence.getInstance().getReference().child("restaurants").child("redChillies");

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID).child("currentOrder").child("activeUsers").child(Details.USER_ID);
        mTableRef.child("name").setValue(Details.USERNAME);
        mTableRef.child("confirmStatus").setValue("no");

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

                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
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

        mMenuRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("menu").child("categories");
        mMenuRef.addValueEventListener(mMenuRefListener);


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



/*
    private void setupDishesID() {

        mMenuDishesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int j = 0;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    cIdArray[j] = childSnapshot.getKey();
                    Log.e("TabbedMenu", cIdArray[j]);

                    if (cIdArray[j] != null) {
                        Details.CUISINE_ID_ARRAY.add(cIdArray[j]);
                        j++;
                    }
                }
                setupViewPager();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TabbedMenu", "DatabaseError onCancelled" + databaseError.toString());
            }
        });
    }
*/

    private void setupViewPager() {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        Details.CUISINE_INDEX = 0;
//        for(int i=0; i<tabsInfo.size(); i++){
//            for(int j=i+1; j<tabsInfo.size(); j++){
//                if(Integer.parseInt(tabsInfo.get(i).getCatPriority())>Integer.parseInt(tabsInfo.get(j).getCatPriority())){
//                    Tabs temp = new Tabs();
//                    temp = tabsInfo.get(i);
//                    tabsInfo.re.get(i) = tabsInfo.get(j);
//
//                }
//            }
//        }
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
            Log.e("TabbedMenu","Shared Preferences Check --- UserID : "+preferences.getString("userID", "")
                    +"username : "+preferences.getString("username", "")
                    +"restID : "+preferences.getString("restaurantId", "")
                    +"tableId : "+preferences.getString("currentTableId", ""));
            Details.REST_ID = "";
            Details.TABLE_ID = "";
            //onStart();
            startActivity(setupIntent);
            return true;
        }
        else if(id == R.id.action_offers){
            Intent offerIntent = new Intent(TabbedMenu.this, OffersActivity.class);
            startActivity(offerIntent);
        }
        else if(id == R.id.action_signout){
            Intent setupIntent = new Intent(TabbedMenu.this, LoginActivity.class);
            //setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("userID");
            editor.remove("username");
            editor.remove("restaurantId");
            editor.remove("currentTableId");
            editor.commit();
            Log.e("TabbedMenu","Shared Preferences Check --- UserID : "+preferences.getString("userID", "")
                    +"username : "+preferences.getString("username", "")
                    +"restID : "+preferences.getString("restaurantId", "")
                    +"tableId : "+preferences.getString("currentTableId", ""));
            Details.USER_ID = "";
            Details.USERNAME = "";
            //Log.e("TabbedMenu","mAuth : "+mAuth.getCurrentUser().getUid());
            mAuth.signOut();
            //onStart();
            startActivity(setupIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragmen  t.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        public PlaceholderFragment() {
//        }
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.tab_fragment, container, false);
//            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
//            return rootView;
//        }
//    }



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
        Log.e("TabbedMenu","Shared Preferences Check --- UserID : "+preferences.getString("userID", "")
                +"username : "+preferences.getString("username", "")
                +"restID : "+preferences.getString("restaurantId", "")
                +"tableId : "+preferences.getString("currentTableId", ""));
        final String restaurantId = preferences.getString("restaurantId", "");
        final String currentTableId = preferences.getString("currentTableId","");
        String userID = preferences.getString("userID", "");
        String username = preferences.getString("username","");
        /*Intent tomain = new Intent(TabbedMenu.this, QR_Offers_prevOrders.class);
        startActivity(tomain);*/
        if(userID==null || userID.isEmpty() || mAuth.getCurrentUser().getUid()==null){
            Intent tomain = new Intent(TabbedMenu.this, LoginActivity.class);
            tomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(tomain);
        }
        try {
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
                            if (restaurantId.equals(null) || restaurantId.isEmpty() || currentTableId.equals(null) || currentTableId.isEmpty()) {
                                Intent tomain = new Intent(TabbedMenu.this, QR_Offers_prevOrders.class);
                                startActivity(tomain);
                            }
                         /*if (flag == 0) {
                             Log.e("TabbedMenu", "In flag==0. Intent should open");
                             Intent toSetup = new Intent(TabbedMenu.this, SetupAcitivty.class);
                             startActivity(toSetup);
                         }*/
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                mDatabaseUsers.addValueEventListener(mDatabaseUsersListener);
        }
        catch (Exception e){
            Log.e("TabbedMenu","Error : " + e);
        }
        Log.e("TabbedMenu","The details are : "+ restaurantId +" "+ currentTableId+ " " + userID+" " + username);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMenuRef.removeEventListener(mMenuRefListener);
        mDatabaseUsers.removeEventListener(mDatabaseUsersListener);
        mRestRef.removeEventListener(mRestRefListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID).child("currentOrder").child("activeUsers").child(Details.USER_ID);
        mTableRef.child("name").setValue(Details.USERNAME);
        mTableRef.child("confirmStatus").setValue("no");
        mMenuRef.addValueEventListener(mMenuRefListener);
        mRestRef.addValueEventListener(mRestRefListener);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    /*public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                default:
                    return null;

            }
        }

           @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle (int position){
            switch (position){
                case 0:
                    return "Indian";
                case 1:
                    return "Chinese";
                case 2:
                    return "Beverages";
                case 3:
                    return "Continental";
            }
                return null;
        }
    }*/
}
