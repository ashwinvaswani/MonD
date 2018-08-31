package com.zconnect.mondiner.customer.viewholders;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.Menu;
import com.zconnect.mondiner.customer.utils.Details;

/**
 * Created by grandiose on 15/3/18.
 */

public class AddOnViewHolder extends RecyclerView.ViewHolder {
    private SimpleDraweeView addOnImage;
    private TextView addOnName;
    private TextView addOnPrice;
    private Button decrement;
    private Button increment;
    private TextView quantityDisplay;
    private Menu addOn;
    private String dishId;
    private final DatabaseReference mTableRef;
    public AddOnViewHolder(View itemView){
        super(itemView);
        addOnImage = itemView.findViewById(R.id.add_on_image);
        addOnName = itemView.findViewById(R.id.add_on_name);
        addOnPrice = itemView.findViewById(R.id.add_on_price);
        decrement = itemView.findViewById(R.id.add_on_decrement_button);
        increment = itemView.findViewById(R.id.add_on_increment_button);
        quantityDisplay = itemView.findViewById(R.id.add_on_quantity_display);
        this.mTableRef = FirebaseDatabase.getInstance().getReference()
                .child("restaurants")
                .child(Details.REST_ID)
                .child("table")
                .child(Details.TABLE_ID)
                .child("currentOrder")
                .child("cart");
    }

    public void populate(final Menu addOn, final String dishId, String rsSymbol, Context context) {
        this.addOn = addOn;
        this.dishId = dishId;
        addOnName.setText(addOn.getItemName());
        addOnPrice.setText(rsSymbol+addOn.getItemPrice());
        quantityDisplay.setText(addOn.getItemQuantity());
        if(!addOn.getImageUri().equalsIgnoreCase("")) {
            Uri uri = Uri.parse(addOn.getImageUri());
            addOnImage.setImageURI(uri);
        }
        int i = Integer.parseInt(addOn.getItemQuantity());
        if(i <= 0)
        {
            increment.setBackgroundResource(R.drawable.round_button_layout_white);
            decrement.setBackgroundResource(R.drawable.round_button_layout_white);
            addOnName.setTextColor(context.getResources().getColor(R.color.text));
        }
        else
        {
            increment.setBackgroundResource(R.drawable.round_button_layout);
            decrement.setBackgroundResource(R.drawable.round_button_layout);
            addOnName.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=0;
                i = Integer.parseInt(addOn.getItemQuantity()) + 1;
                increment.setBackgroundResource(R.drawable.round_button_layout);
                decrement.setBackgroundResource(R.drawable.round_button_layout);
                addOn.setItemQuantity(i + "");
                mTableRef.child(dishId).child("price").setValue(addOn.getItemPrice());
                mTableRef.child(dishId).child("name").setValue(addOn.getItemName());
                mTableRef.child(dishId).child("users").child(Details.USER_ID).setValue(addOn.getItemQuantity());
            }
        });
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=0;
                i = Integer.parseInt(addOn.getItemQuantity()) - 1;
                if(i<=0){
                    mTableRef.child(dishId).child("users").child(Details.USER_ID).setValue("0");
                    quantityDisplay.setText("0");
                    mTableRef.child(dishId).child("price").setValue(addOn.getItemPrice());
                    mTableRef.child(dishId).child("name").setValue(addOn.getItemName());
                    increment.setBackgroundResource(R.drawable.round_button_layout_white);
                    decrement.setBackgroundResource(R.drawable.round_button_layout_white);
                    /*addItem.setVisibility(View.VISIBLE);
                    decrement.setVisibility(View.GONE);
                    increment.setVisibility(View.GONE);
                    quantityDisplay.setVisibility(View.GONE);*/
                /*if (Integer.parseInt(menu.getItemQuantity()) < 0) {
                    mTableRef.child(dishId).child("users").child(Details.USER_ID).setValue("0");
                    menu.setItemQuantity("0");*/
                }
                else {
                    i = Integer.parseInt(addOn.getItemQuantity()) - 1;
                    addOn.setItemQuantity(i + "");
                    mTableRef.child(dishId).child("price").setValue(addOn.getItemPrice());
                    mTableRef.child(dishId).child("name").setValue(addOn.getItemName());
                    mTableRef.child(dishId).child("users").child(Details.USER_ID).setValue(addOn.getItemQuantity());
                    /*addItem.setVisibility(View.GONE);
                    decrement.setVisibility(View.VISIBLE);
                    increment.setVisibility(View.VISIBLE);
                    quantityDisplay.setVisibility(View.VISIBLE);*/
                }
            }
        });

    }


}
