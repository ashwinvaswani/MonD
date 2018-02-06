package com.zconnect.mondiner.customer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.mondiner.customer.adapters.MenuAdapter;
import com.zconnect.mondiner.customer.models.Menu;
import com.zconnect.mondiner.customer.models.Tabs;
import com.zconnect.mondiner.customer.utils.Details;

import java.util.ArrayList;

public class TabFragment extends Fragment {

    private RecyclerView indianMenu;
    private TextView noItemText;

    private Tabs tabInfo = new Tabs();

    private com.google.firebase.database.Query mMenuRef;
    private DatabaseReference mTableRef;
    private ValueEventListener tableListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            for (DataSnapshot dish : dataSnapshot.getChildren()) {
                if (!(dish.child("users").hasChild(Details.USER_ID) && dishIDs.contains(dish.getKey()))) continue;
                int i = dishIDs.indexOf(dish.getKey());
                menus.get(i).setItemQuantity(dish.child("users").child(Details.USER_ID).getValue(String.class));
                String quantity = menus.get(i).getItemQuantity();
                int totalQuantity = 0;
                totalQuantity = Integer.parseInt(quantity);
                for(DataSnapshot user : dish.child("users").getChildren()) {
                    if(!user.getKey().equals(Details.USER_ID)) {
                        totalQuantity = totalQuantity+Integer.parseInt(user.getValue(String.class));
                    }
                    else{
                        continue;
                    }
                }
                mTableRef.child(dish.getKey()).child("quantity").setValue(totalQuantity + "");
            }
            menuAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };
    private MenuAdapter menuAdapter;
    private ArrayList<Menu> menus = new ArrayList<>();
    private ArrayList<String> dishIDs = new ArrayList<>();
    public TabFragment() {}

    public static TabFragment getInstance(Tabs tabInfo) {
        TabFragment tab = new TabFragment();
        tab.tabInfo = tabInfo;
        return tab;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_fragment, container, false);
        indianMenu = rootView.findViewById(R.id.indian_menu_rv);
        noItemText = rootView.findViewById(R.id.no_item_text);
        indianMenu.setLayoutManager(new LinearLayoutManager(getContext()));
        noItemText.setText("No items to display for the cuisine : " + tabInfo.getCatName());
        Log.e("TabFragment", "" + Details.REST_ID + "current_cuisine : " + Details.Current_Cuisine);

        Log.e("TabFragment", "Cuisine id index " + Details.CUISINE_INDEX);

        //TODO : Change all hard coded stuff
        mMenuRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("menu").child("dishes").orderByChild("category").equalTo(tabInfo.getCatName());
        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child("redChillies").child("table").child(Details.TABLE_ID).child("currentOrder")/*.child("dishes")*/.child("cart");
        menuAdapter = new MenuAdapter(menus, dishIDs);
        indianMenu.setAdapter(menuAdapter);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMenuRef.addValueEventListener(new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                menus.clear();
                dishIDs.clear();
                //TODO : Handle null pointer exception for price and type
                for (final DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    try {
                        if (childSnapshot.child("name").exists() &&
                                childSnapshot.child("price").exists() &&
                                childSnapshot.child("type").exists() &&
                                childSnapshot.child("availability").getValue(String.class).equalsIgnoreCase("true")) {
                            Menu m = new Menu();
                            dishIDs.add(childSnapshot.getKey());
                            m.setItemName(childSnapshot.child("name").getValue(String.class));
                            m.setItemPrice(childSnapshot.child("price").getValue(String.class));
                            m.setVegNonVeg(childSnapshot.child("type").getValue(String.class));
                            menus.add(m);
                        }
                    }
                    catch (Exception e){
                        Log.e("TabFragment",""+e);
                    }
                }
                if (menus.size() != 0) {
                    noItemText.setVisibility(View.GONE);
                }
                menuAdapter.notifyDataSetChanged();
                mTableRef.addValueEventListener(tableListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TabFragment", "on cancelled" + databaseError.toString());
            }
        });

    }

    @Override
    public void onPause() {
        mTableRef.removeEventListener(tableListener);
        super.onPause();
    }
}

