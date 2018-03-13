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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.mondiner.customer.adapters.CartAdapter;
import com.zconnect.mondiner.customer.models.DishOrdered;
import com.zconnect.mondiner.customer.utils.Details;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class ConfirmationActivity extends AppCompatActivity {

    private RecyclerView cartContent;
    private DatabaseReference mTableRef;
    private ValueEventListener mTableRefListener;
    private ValueEventListener confirmStatusListener;
    private DatabaseReference mDataRef;
    private CartAdapter cartAdapter;
    private ArrayList<DishOrdered> dishitems = new ArrayList<>();
    private int dishAmount;
    private String dishPrice;
    private int dishQuantity;
    private String quantity;
    private TextView totalAmount;
    private TextView orderStatus;
    private int amount =0;
    private Button callWaiter;
    private Button orderAdd;
    private LinearLayout amountLinear;
    private TextView yourOrder;
    //private GifImageView gif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.cart_toolbar);
        setSupportActionBar(toolbar);

        //TODO : Align the toolbar text to centre
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.center_text_bill_title);

        cartContent = findViewById(R.id.order_confirm_rv);
        totalAmount = findViewById(R.id.confirm_total_amount);
        callWaiter = findViewById(R.id.call_waiter);
        orderAdd = findViewById(R.id.order_add);
        orderStatus = findViewById(R.id.order_status);
        orderStatus.setText("Processing Order");
        amountLinear = findViewById(R.id.amount_linear);
        yourOrder = findViewById(R.id.your_order);


        //orderAdd.setClickable(false);
        //orderAdd.setBackgroundResource(R.color.hint);
        /*gif = findViewById(R.id.loading_gif);
        gif.setVisibility(View.VISIBLE);*/

        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID).child("currentOrder");
        mDataRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID);
        cartContent.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(dishitems, getApplicationContext());
        cartContent.setAdapter(cartAdapter);
        dishAmount = 0;
        //TODO : Handle null exceptions from firebase
        mTableRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dishitems.clear();
                amount=0;
                for (final DataSnapshot dishid : dataSnapshot.getChildren()) {
                    final DishOrdered dishOrdered = new DishOrdered();
                    try {
                        if (!(dishid.child("quantity").getValue(Integer.class) == 0)) {
                            dishOrdered.setDishID(dishid.getKey());
                            dishOrdered.setDishName(dishid.child("name").getValue(String.class));
                            dishOrdered.setDishPrice(dishid.child("price").getValue(String.class));
                            dishOrdered.setDishQuantity(dishid.child("quantity").getValue(Integer.class));
                            dishQuantity = dishid.child("quantity").getValue(Integer.class);
                            dishAmount = dishQuantity * Integer.parseInt(dishid.child("price").getValue(String.class));
                            dishOrdered.setDishAmount(dishAmount + "");
                            amount += dishAmount;
                            Log.e("amount : ", "" + amount);
                            dishitems.add(dishOrdered);
                            totalAmount.setText(getResources().getString(R.string.Rs) + amount);
                        }
                    } catch (Exception e) {
                        Log.e("ConfirmationActivity", "" + e);
                    }
                }
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        try {
            mTableRef.child("bill").addValueEventListener(mTableRefListener);
        }catch (Exception e){

        }

        try {
            confirmStatusListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("confirmStatus").getValue(String.class).equalsIgnoreCase("true")) {
                        cartContent.setVisibility(View.VISIBLE);
                        amountLinear.setVisibility(View.VISIBLE);
                        yourOrder.setVisibility(View.VISIBLE);
                        orderStatus.setText("Order Confirmed");
                        //orderAdd.setClickable(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }catch (Exception e){

        }
        mDataRef.addValueEventListener(confirmStatusListener);

        callWaiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDataRef.child("callWaiter").setValue("true");
                Toast.makeText(ConfirmationActivity.this, "Please wait! Waiter will attend you.", Toast.LENGTH_SHORT).show();
            }
        });


        orderAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMenu = new Intent(ConfirmationActivity.this, TabbedMenu.class);
                toMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(toMenu);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cartContent.setVisibility(View.GONE);
        amountLinear.setVisibility(View.GONE);
        yourOrder.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTableRef.child("cart").removeEventListener(mTableRefListener);
        mDataRef.removeEventListener(confirmStatusListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mTableRef.child("cart").addValueEventListener(mTableRefListener);
        mDataRef.addValueEventListener(confirmStatusListener);
    }

    @Override
    protected void onResume() {
        super.onResume();/*
        cartContent.setVisibility(View.GONE);
        amountLinear.setVisibility(View.GONE);
        yourOrder.setVisibility(View.GONE);*/
    }
}
