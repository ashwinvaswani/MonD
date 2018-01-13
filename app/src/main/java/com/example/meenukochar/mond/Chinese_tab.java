package com.example.meenukochar.mond;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.meenukochar.mond.adapters.MenuAdapter;
import com.example.meenukochar.mond.models.Menu;
import com.example.meenukochar.mond.utils.Details;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Ishaan on 09-01-2018.
 */

public class Chinese_tab extends Fragment {

    RecyclerView chineseMenu;
    TextView chineseText;
    //ArrayList<Menu> menus = new ArrayList<Menu>();

    DatabaseReference mMenuRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chinese_tab, container, false);

        chineseMenu = rootView.findViewById(R.id.chinese_menu_rv);
        chineseText = rootView.findViewById(R.id.chinese_text);
        Log.e("Chinese_tab",""+ Details.REST_ID);
        mMenuRef = FirebaseDatabase.getInstance().getReference().child("restaurantID").child(Details.REST_ID).child("menu").child("dishes");

        mMenuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Menu> menus = new ArrayList<Menu>();
                int i = 0;

                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){

                    if(childSnapshot.child("cuisine").exists() && childSnapshot.child("cuisine").getValue(String.class).equals("chinese") &&
                            childSnapshot.child("price").exists() &&
                            childSnapshot.child("type").exists()) {
                        Log.e("Indian_tab", "Dish :" + childSnapshot.child("name").getValue(String.class));
                        menus.add(new Menu());
                        menus.get(i).setItemName(childSnapshot.child("name").getValue(String.class));
                        menus.get(i).setItemPrice(childSnapshot.child("price").getValue(Double.class));
                        menus.get(i).setVegNonVeg(childSnapshot.child("type").getValue(Integer.class));
                        i++;
                    }
                    chineseText.setVisibility(View.GONE);
                    MenuAdapter menuAdapter = new MenuAdapter(menus);
                    chineseMenu.setAdapter(menuAdapter);
                    chineseMenu.setLayoutManager(new LinearLayoutManager(getContext()));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return rootView;
    }

}
