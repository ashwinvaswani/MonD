package com.zconnect.mondiner.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.facebook.drawee.view.SimpleDraweeView;
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

    private DatabaseReference mCurrentOrderRef;
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
        setTitle("Cart");

        android.support.v7.widget.Toolbar toolbar =  findViewById(R.id.cart_toolbar);
        setSupportActionBar(toolbar);
        //TODO : Align the toolbar text to centre
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.center_text_title_bar);

        cartContent = findViewById(R.id.cart_recyclerview);
        cartUserDatarv = findViewById(R.id.user_cart_status);
        cartUserDatarv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cartUserDatarv.setAdapter(new UserConfirmationAdapter(new ArrayList<CartUserData>()));
        confirmOrder = findViewById(R.id.confirm_order);
        noItemCart = findViewById(R.id.no_item_cart_text);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        cartContent.setLayoutManager(linearLayoutManager);
        userConfirmationAdapter = new UserConfirmationAdapter(userData);
        cartUserDatarv.setAdapter(userConfirmationAdapter);
        totalAmount = findViewById(R.id.total_text);
        mCurrentOrderRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID).child("currentOrder");

        cartAdapter = new CartAdapter(dishitems, getApplicationContext());
        cartContent.setAdapter(cartAdapter);

        dishAmount = 0;
        //TODO : Handle null exceptions from firebase
        mCurrentOrderRef.child("cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dishitems.clear();
                amount =0;
                for (final DataSnapshot dishid : dataSnapshot.getChildren()) {
                    final DishOrdered dishOrdered = new DishOrdered();
                    try {
                        if (!(Integer.parseInt(dishid.child("quantity").getValue(String.class)) == 0)) {
                            dishOrdered.setDishID(dishid.getKey());
                            dishOrdered.setDishName(dishid.child("name").getValue(String.class));
                            dishOrdered.setDishPrice(dishid.child("price").getValue(String.class));
                            dishOrdered.setDishQuantity(dishid.child("quantity").getValue(String.class));
                            dishQuantity = Integer.parseInt(dishid.child("quantity").getValue(String.class));
                            dishAmount = Integer.parseInt(dishid.child("quantity").getValue(String.class)) * Integer.parseInt(dishid.child("price").getValue(String.class));
                            dishOrdered.setDishAmount(""+dishAmount);
                            amount += dishAmount;
                            Log.e("amount : ", "" + amount);
                            dishitems.add(dishOrdered);
                            totalAmount.setText(getResources().getString(R.string.Rs) + amount);
                            if (dishitems.size() != 0) {
                                noItemCart.setVisibility(View.GONE);
                            }
                        }
                    }
                    catch(Exception e){
                        Log.e("CartActivity",""+e);
                    }
                }
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        /*mCurrentOrderRef.child("activeUsers").addValueEventListener(new ValueEventListener() {
            @   Override
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
        });*/
        cartUserDatarv.setVisibility(View.GONE);
        confirmOrder.setVisibility(View.VISIBLE);
        mCurrentOrderRef.child("activeUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userData.clear();
                for (DataSnapshot userID : dataSnapshot.getChildren()) {
                    final CartUserData cartUserData = new CartUserData();
                    cartUserData.setUserID(userID.getKey());
                    String userName = userID.child("name").getValue(String.class);
                    String userNameParts[] = userName.trim().split(" ");
                    cartUserData.setUserName(userNameParts[0]);
                    cartUserData.setUserStatus(userID.child("confirmStatus").getValue(String.class));
                    //userData.add(cartUserData);
                    userData.add(cartUserData);

                }
                userConfirmationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentOrderRef.child("activeUsers").child(Details.USER_ID).child("confirmStatus").setValue("yes");
//              userConfirmationAdapter.notifyDataSetChanged();
                confirmOrder.setVisibility(View.GONE);
                cartUserDatarv.setVisibility(View.VISIBLE);
            }
        });

        mCurrentOrderRef.child("activeUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int flag=0;
                for(DataSnapshot user : dataSnapshot.getChildren()){
                        if(user.child("confirmStatus").getValue(String.class).equalsIgnoreCase("no")){
                        flag=1;
                    }
                }
                if(flag==0){
                    mCurrentOrderRef.child("orderConfirmation").setValue("yes");
                    Toast.makeText(CartActivity.this, "Order confirmed by all users.", Toast.LENGTH_SHORT).show();
                    Intent setupIntent = new Intent(CartActivity.this, ConfirmationActivity .class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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






