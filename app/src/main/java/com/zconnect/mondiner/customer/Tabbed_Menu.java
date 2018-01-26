package com.zconnect.mondiner.customer;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.mondiner.customer.models.Tabs;
import com.zconnect.mondiner.customer.utils.Details;

import java.util.ArrayList;
import java.util.List;

public class Tabbed_Menu extends AppCompatActivity {

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
    TabLayout tabLayout;
    DatabaseReference mMenuRef;
    private DatabaseReference mTableRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed__menu);


        //incDec = findViewById(R.id.inc_dec);
        String rest_id = getIntent().getStringExtra("rest_id");
        String table_id = getIntent().getStringExtra("table_id");
        //Details.REST_ID = rest_id;
        //Details.TABLE_ID = table_id;
        Log.e("Tabbed_Menu", "rest_id" + rest_id + "table_id" + table_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        noItemText = findViewById(R.id.no_item_text);
        tabLayout = findViewById(R.id.tabs);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        //TODO : get the REST_ID from the Details class. Right now its hard coded
        //mMenuRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child("redChillies");

        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table").child(Details.TABLE_ID).child("currentOrder").child("activeUsers").child(Details.USER_ID).child("name");
        mTableRef.setValue(Details.USERNAME);

        //mMenuRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child("redChillies").child("info").child("servesCuisine");
        mMenuRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("menu").child("categories");
        mMenuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
       //         Log.e("Tabbed_Menu:", "in onDataChange" + dataSnapshot.child("info")
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
//                Log.e("Tabbed_Menu:", "in onDataChange" + dataSnapshot.getValue(String.class));
//                String cuisine = dataSnapshot.getValue(String.class);
//                cArray = cuisine.split(",");
//                for(int i=0; i<cArray.length; i++){
//                    cArray[i] = cArray[i].trim();
//                }
//                Arrays.sort(cArray);
//                counter = 0;
//                for (String aCArray : cArray) {
//                    Log.e("Tabbed_Menu", aCArray);
//                    if (aCArray != null) {
//                        Details.CUISINE_ARRAY.add(aCArray);
//                        counter++;
//                        Log.e("Tabbed_Menu :", "counter - " + counter);
//                    }
//                }
//                cIdArray = new String[counter];

//                setupDishesID();
                setupViewPager();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Tabbed_Menu", "DatabaseError onCancelled" + databaseError.toString());
            }
        });


        //fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent tomain = new Intent(Tabbed_Menu.this, CartActivity.class);
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
                    Log.e("Tabbed_Menu", cIdArray[j]);

                    if (cIdArray[j] != null) {
                        Details.CUISINE_ID_ARRAY.add(cIdArray[j]);
                        j++;
                    }
                }
                setupViewPager();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Tabbed_Menu", "DatabaseError onCancelled" + databaseError.toString());
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

//            Log.e("Tabbed_Menu -- ", "cArray[i] : " + cArray[i] + "cIdArray : " + cIdArray[i] + " Current Cuisines : " + Details.CUISINE_ID_ARRAY.toString());
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
//        if (id == R.id.action_settings) {
//            return true;
//        }

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
            //Log.e("Tabbed_Menu", "no of frags " + mFragmentList.size());
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
//            noItemText.setText("No" + title + "items to show");
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
