package com.zconnect.mondiner.customer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.mondiner.customer.adapters.DishQuantityDetailsAdapter;
import com.zconnect.mondiner.customer.models.CartUserData;
import com.zconnect.mondiner.customer.utils.Details;

import java.util.ArrayList;

public class DishQuantityDetails extends AppCompatActivity {

    private RecyclerView userQuantityrv;
    private String dishId;
    private DatabaseReference mTableRef;
    private DishQuantityDetailsAdapter dishQuantityDetailsAdapter;
    ArrayList<CartUserData> userData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details_for_item);
        userQuantityrv = findViewById(R.id.user_details_rv);
        userQuantityrv.setLayoutManager(new LinearLayoutManager(this));
        userQuantityrv.setAdapter(new DishQuantityDetailsAdapter(new ArrayList<CartUserData>()));
        Bundle bundle = getIntent().getExtras();
        dishId = bundle.getString("dishID");
        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID).child("currentOrder");

        mTableRef.child("activeUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userID : dataSnapshot.getChildren()) {
                    final CartUserData cartUserData = new CartUserData();
                    cartUserData.setUserID(userID.getKey());
                    cartUserData.setUserName(userID.child("name").getValue(String.class));
                    mTableRef.child("cart").child(dishId).child("users").child(userID.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            cartUserData.setUserQuantity(dataSnapshot.getValue(String.class));
                            userData.add(cartUserData);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                dishQuantityDetailsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dishQuantityDetailsAdapter = new DishQuantityDetailsAdapter(userData);
    }
}
