package com.zconnect.mondiner.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.mondiner.customer.adapters.CartAdapter;
import com.zconnect.mondiner.customer.adapters.UserConfirmationAdapter;
import com.zconnect.mondiner.customer.models.CartUserData;
import com.zconnect.mondiner.customer.models.DishOrdered;
import com.zconnect.mondiner.customer.utils.Details;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private DatabaseReference mTableRef;
    private RecyclerView cartContent;
    private RecyclerView cartUserDatarv;
    private Button confirmOrder;
    private DatabaseReference mDishRef;
    private CartAdapter cartAdapter;
    private UserConfirmationAdapter userConfirmationAdapter;
    ArrayList<CartUserData> userData = new ArrayList<>();
    private ArrayList<DishOrdered> dishitems = new ArrayList<>();
    private int dishAmount;
    private String dishPrice;
    private int dishQuantity;
    private String quantity;
    private TextView totalAmount;
    private int amount = 0;
    private TextView noItemCart;
    private long size=0;
    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartContent = findViewById(R.id.cart_recyclerview);
        cartUserDatarv = findViewById(R.id.user_cart_status);
        cartUserDatarv.setLayoutManager(new LinearLayoutManager(this));
        cartUserDatarv.setAdapter(new UserConfirmationAdapter(new ArrayList<CartUserData>()));
        confirmOrder = findViewById(R.id.confirm_order);
        noItemCart = findViewById(R.id.no_item_cart_text);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        cartContent.setLayoutManager(linearLayoutManager);

        totalAmount = findViewById(R.id.total_text);
        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID).child("currentOrder");

        cartAdapter = new CartAdapter(dishitems);
        cartContent.setAdapter(cartAdapter);

        dishAmount = 0;
        //TODO : Handle null exceptions from firebase
        mTableRef.child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dishitems.clear();

                for (final DataSnapshot dishid : dataSnapshot.getChildren()) {
                    final DishOrdered dishOrdered = new DishOrdered();

                    if (!(Integer.parseInt(dishid.child("quantity").getValue(String.class)) == 0)) {
                        dishOrdered.setDishID(dishid.getKey());
                        dishOrdered.setDishName(dishid.child("name").getValue(String.class));
                        dishOrdered.setDishPrice(dishid.child("price").getValue(String.class));
                        dishOrdered.setDishQuantity(dishid.child("quantity").getValue(String.class));
                        dishQuantity = Integer.parseInt(dishid.child("quantity").getValue(String.class));
                        dishAmount = Integer.parseInt(dishid.child("quantity").getValue(String.class)) * Integer.parseInt(dishid.child("price").getValue(String.class));
                        dishOrdered.setDishAmount(dishAmount + "");
                        amount += dishAmount;
                        Log.e("amount : ", "" + amount);
                        dishitems.add(dishOrdered);
                        totalAmount.setText(amount + "");
                        if (dishitems.size() != 0) {
                            noItemCart.setVisibility(View.GONE);
                        }
                    }
                }
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        mTableRef.child("activeUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userID : dataSnapshot.getChildren()) {
                    size = userID.getChildrenCount();
                    if(userID.child("confirmStatus").getValue(String.class).equalsIgnoreCase("Yes")){
                        counter++;
                    }
                }
                Log.e("CartActivity"," -- size : "+size+"counter : "+counter);
                if(counter==size){
                    Intent setupIntent = new Intent(CartActivity.this, ConfirmationActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTableRef.child("activeUsers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userID : dataSnapshot.getChildren()) {
                                final CartUserData cartUserData = new CartUserData();
                                cartUserData.setUserID(userID.getKey());
                                cartUserData.setUserName(userID.child("name").getValue(String.class));
                                if (userID.getKey().equals(Details.USER_ID)){
                                    mTableRef.child("activeUsers").child(userID.getKey()).child("confirmStatus").setValue("yes");

                                }
                            cartUserData.setUserStatus(userID.child("confirmStatus").getValue(String.class));
                            userData.add(cartUserData);
                            confirmOrder.setVisibility(View.GONE);
                            cartUserDatarv.setVisibility(View.VISIBLE);
                            /*
                                else{
                                    cartUserData.setUserStatus(userID.child("confirmStatus").getValue(String.class));
                                    userData.add(cartUserData);
                                    confirmOrder.setVisibility(View.GONE);
                                    cartUserDatarv.setVisibility(View.VISIBLE);
                                }*/
                        }
                        userConfirmationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                userConfirmationAdapter = new UserConfirmationAdapter(userData);
                cartUserDatarv.setAdapter(userConfirmationAdapter);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*Intent setupIntent = new Intent(CartActivity.this, ConfirmationActivity .class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(setupIntent);*/
    }
}






