package com.zconnect.mondiner.customer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.mondiner.customer.adapters.CartAdapter;
import com.zconnect.mondiner.customer.models.CartUserData;
import com.zconnect.mondiner.customer.models.DishOrdered;
import com.zconnect.mondiner.customer.utils.Details;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private DatabaseReference mTableRef;
    private RecyclerView cartContent;
    private DatabaseReference mDishRef;
    private CartAdapter cartAdapter;
    ArrayList<CartUserData> userData = new ArrayList<>();
    ArrayList<DishOrdered> dishitems = new ArrayList<>();
    private int dishAmount;
    private String dishPrice;
    private int dishQuantity;
    private String quantity;
    private TextView totalAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartContent = findViewById(R.id.cart_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartContent.setLayoutManager(linearLayoutManager);

        totalAmount = findViewById(R.id.total_text);
        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID).child("currentOrder").child("dishes");
        mDishRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("menu")
                .child("dishes");

/*        mTableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    userData.add(new CartUserData());
                    userData.get(i).setUserID(childSnapshot.getKey());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        dishAmount = 0;

        mTableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dishitems.clear();

                for (final DataSnapshot dishid : dataSnapshot.getChildren()) {
                    final  DishOrdered dishOrdered = new DishOrdered();
                    dishOrdered.setDishID(dishid.getKey());

                    mDishRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dishOrdered.setDishPrice(dataSnapshot.child(dishid.getKey().toString()).child("price").getValue().toString());
                            dishPrice = dataSnapshot.child(dishid.getKey().toString()).child("price").getValue().toString();
                            dishOrdered.setDishName(dataSnapshot.child(dishid.getKey().toString()).child("name").getValue().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                    dishQuantity = 0;
                    int i=0;
                    for(DataSnapshot useridshot : dishid.getChildren()){
                        //userData.get(i).setUserID(useridshot.getValue(String.class));
                        quantity = useridshot.child("quantity").getValue().toString();
                        dishQuantity = dishQuantity + Integer.parseInt(quantity);
 //                       i++;
                    }

                    dishOrdered.setDishQuantity(dishQuantity+"");
                    dishitems.add(dishOrdered);
//                    dishAmount += Integer.parseInt(dishPrice)*dishQuantity;

                }


                cartAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //totalAmount.setText(Integer.toString(dishAmount));
        cartAdapter = new CartAdapter(dishitems);
        cartContent.setAdapter(cartAdapter);
    }

}



