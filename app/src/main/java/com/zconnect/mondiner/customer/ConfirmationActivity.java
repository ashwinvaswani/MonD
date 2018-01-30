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

public class ConfirmationActivity extends AppCompatActivity {

    private RecyclerView cartContent;
    private DatabaseReference mTableRef;
    private DatabaseReference mDataRef;
    private CartAdapter cartAdapter;
    private ArrayList<DishOrdered> dishitems = new ArrayList<>();
    private int dishAmount;
    private String dishPrice;
    private int dishQuantity;
    private String quantity;
    private TextView totalAmount;
    private int amount =0;
    private Button callWaiter;
    private Button orderAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        cartContent = findViewById(R.id.order_confirm_rv);
        totalAmount = findViewById(R.id.confirm_total_amount);
        callWaiter = findViewById(R.id.call_waiter);
        orderAdd = findViewById(R.id.order_add);
        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID).child("currentOrder");
        mDataRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID);
        cartContent.setLayoutManager(new LinearLayoutManager(this));
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
                        amount+=dishAmount;
                        Log.e("amount : ",""+amount);
                        dishitems.add(dishOrdered);
                        totalAmount.setText(amount + "");
                    }
                }
                cartAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        callWaiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDataRef.child("callWaiter").setValue("true");
                Toast.makeText(ConfirmationActivity.this, "Please wait! Waiter coming...", Toast.LENGTH_SHORT).show();
            }
        });


        orderAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMenu = new Intent(ConfirmationActivity.this, Tabbed_Menu.class);
                toMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(toMenu);
            }
        });
    }

}
