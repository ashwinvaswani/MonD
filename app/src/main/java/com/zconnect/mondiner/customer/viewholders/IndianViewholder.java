package com.zconnect.mondiner.customer.viewholders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.mondiner.customer.R;


/**
 * Created by Ishaan on 11-01-2018.
 */

public class IndianViewholder extends RecyclerView.ViewHolder {

    public TextView itemNametv;
    public TextView itemPricetv;
    public TextView itemVegtv;
    public Button addItem;
    public Button decrement;
    public Button increment;
    public TextView quantityDisplay;
    public ElegantNumberButton incDec;
    private DatabaseReference mTableRef;
    private int quantity = 0;

    public IndianViewholder(View itemView) {

        super(itemView);

        itemNametv = itemView.findViewById(R.id.item_name_indian);
        itemPricetv = itemView.findViewById(R.id.item_price_indian);
        itemVegtv = itemView.findViewById(R.id.item_veg_indian);
        decrement = itemView.findViewById(R.id.decrement_button);
        increment = itemView.findViewById(R.id.increment_button);
        quantityDisplay = itemView.findViewById(R.id.quantity_display);
        //addItem = itemView.findViewById(R.id.add_btn_frame);
        //incDec = itemView.findViewById(R.id.inc_dec);
        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child("redChillies").child("table").child("redChilliesTable_02");
        /*addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem.setEnabled(false);
                incDec.setEnabled(true);
                incDec.setNumber("1");
            }
        });
        incDec.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

               /* if(incDec.getNumber().equals(0)){
                    addItem.setVisibility(View.VISIBLE);
                    incDec.setVisibility(View.GONE);
                }

               // else{
                    mTableRef.child("dish").child("name").setValue("Idli");
                    mTableRef.child("dish").child("quantity").setValue(newValue + "");
                //}
            }
        });*/
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("IndianViewHolder" +
                        " ","in textview"+quantityDisplay.getText().toString());
                quantity = quantity +1;
                mTableRef.child("dish").child("name").setValue("Idli");
                mTableRef.child("dish").child("quantity").setValue(quantity + "");
                Log.e("IndianViewHolder ",""+quantity);
                updateQuantity();
            }
        });
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = quantity -1;
                if(quantity<0) {
                    quantityDisplay.setText("0");
                    mTableRef.child("dish").child("name").setValue("Idli");
                    mTableRef.child("dish").child("quantity").setValue("0");
                }
                else {
                    mTableRef.child("dish").child("name").setValue("Idli");
                    mTableRef.child("dish").child("quantity").setValue(quantity + "");
                    updateQuantity();
                }
            }
        });

    }

    private void updateQuantity() {
        mTableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("IndianViewHolder", "in if : " + dataSnapshot.child("dish").child("quantity").getValue(String.class));
                quantity = Integer.parseInt(dataSnapshot.child("dish").child("quantity").getValue(String.class));
                quantityDisplay.setText(Integer.toString(quantity));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
