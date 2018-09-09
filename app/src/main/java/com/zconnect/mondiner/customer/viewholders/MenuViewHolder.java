package com.zconnect.mondiner.customer.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.TabbedMenu;
import com.zconnect.mondiner.customer.models.Menu;
import com.zconnect.mondiner.customer.utils.Details;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView itemNametv;
    private TextView itemPricetv;
    private TextView itemVegtv;
    private Button decrement;
    private Button increment;
    private ImageView vegNonVegImage;
    private SimpleDraweeView draweeView;
    //private Button addItem;
    private TextView quantityDisplay;
    private final DatabaseReference mTableRef;
    private Menu menu;
    private String dishId;



    public MenuViewHolder(View itemView) {
        super(itemView);
        itemNametv = itemView.findViewById(R.id.item_name_indian);
        itemPricetv = itemView.findViewById(R.id.item_price_indian);
        decrement = itemView.findViewById(R.id.decrement_button);
        increment = itemView.findViewById(R.id.increment_button);
        quantityDisplay = itemView.findViewById(R.id.quantity_display);
        //addItem = itemView.findViewById(R.id.add_btn);
        vegNonVegImage = itemView.findViewById(R.id.veg_nonveg);
        draweeView = itemView.findViewById(R.id.dish_image);
        this.mTableRef = FirebaseDatabase.getInstance().getReference()
                .child("restaurants")
                .child(Details.REST_ID)
                .child("table")
                .child(Details.TABLE_ID)
                .child("currentOrder")
                .child("cart");
                //.child("dishes");


    }

    public void populate(final com.zconnect.mondiner.customer.models.Menu menu, final String dishId, final String rsSymbol, final Context context) {
        this.menu = menu;
        this.dishId = dishId;
        itemNametv.setText(menu.getItemName());
        itemPricetv.setText(rsSymbol + menu.getItemPrice());
//        itemVegtv.setText(menu.getVegNonVeg());
        quantityDisplay.setText(menu.getItemQuantity());
        if(!menu.getImageUri().equalsIgnoreCase("")) {
            Uri uri = Uri.parse(menu.getImageUri());
            draweeView.setImageURI(uri);
        }
        String vegNonVeg = menu.getVegNonVeg();
        if(vegNonVeg.equalsIgnoreCase("veg")){
            vegNonVegImage.setBackgroundResource(R.drawable.veg_symbol);
        }
        else{
            vegNonVegImage.setBackgroundResource(R.drawable.non_veg_symbol);
        }
        increment.setOnClickListener(this);
        decrement.setOnClickListener(this);
        int i = Integer.parseInt(menu.getItemQuantity());
        if(i <= 0)
        {
            increment.setBackgroundResource(R.drawable.round_button_layout_white);
            decrement.setBackgroundResource(R.drawable.round_button_layout_white);
            itemNametv.setTextColor(context.getResources().getColor(R.color.text));
        }
        else
        {
            increment.setBackgroundResource(R.drawable.round_button_layout);
            decrement.setBackgroundResource(R.drawable.round_button_layout);
            itemNametv.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
        if(!menu.getAvailability()){
            Log.e("MenuViewHold","Name : "+menu.getItemName()+"Availability : "+menu.getAvailability());
            itemNametv.setTextColor(context.getResources().getColor(R.color.hint));
            quantityDisplay.setTextColor(context.getResources().getColor(R.color.hint));
            increment.setEnabled(false);
            decrement.setEnabled(false);
            increment.setBackgroundResource(R.drawable.round_button_disabled_layout);
            decrement.setBackgroundResource(R.drawable.round_button_disabled_layout);
            increment.setTextColor(context.getResources().getColor(R.color.hint));
            decrement.setTextColor(context.getResources().getColor(R.color.hint));
        }
        //addItem.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i=0;

        switch (view.getId()) {
            case R.id.increment_button: {
                Log.e("MenuViewHolder" +
                        " ", "in textview" + quantityDisplay.getText().toString());
                i = Integer.parseInt(menu.getItemQuantity()) + 1;
                increment.setBackgroundResource(R.drawable.round_button_layout);
                decrement.setBackgroundResource(R.drawable.round_button_layout);
                menu.setItemQuantity(i + "");
                mTableRef.child(dishId).child("price").setValue(menu.getItemPrice());
                mTableRef.child(dishId).child("name").setValue(menu.getItemName());
                mTableRef.child(dishId).child("users").child(Details.USER_ID).setValue(menu.getItemQuantity());

                break;
            }
            case R.id.decrement_button: {
                i = Integer.parseInt(menu.getItemQuantity()) - 1;
                if(i<=0){
                    mTableRef.child(dishId).child("users").child(Details.USER_ID).setValue("0");
                    quantityDisplay.setText("0");
                    mTableRef.child(dishId).child("price").setValue(menu.getItemPrice());
                    mTableRef.child(dishId).child("name").setValue(menu.getItemName());
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
                    i = Integer.parseInt(menu.getItemQuantity()) - 1;
                    menu.setItemQuantity(i + "");
                    mTableRef.child(dishId).child("price").setValue(menu.getItemPrice());
                    mTableRef.child(dishId).child("name").setValue(menu.getItemName());
                    mTableRef.child(dishId).child("users").child(Details.USER_ID).setValue(menu.getItemQuantity());
                    /*addItem.setVisibility(View.GONE);
                    decrement.setVisibility(View.VISIBLE);
                    increment.setVisibility(View.VISIBLE);
                    quantityDisplay.setVisibility(View.VISIBLE);*/
                }
                break;
            }/*
            case R.id.add_btn: {
                mTableRef.child(dishId).child(Details.USER_ID).child("quantity").setValue("1");
                menu.setItemQuantity("1");
                mTableRef.child(dishId).child("users").child(Details.USER_ID).setValue(menu.getItemQuantity());
                addItem.setVisibility(View.GONE);
                decrement.setVisibility(View.VISIBLE);
                increment.setVisibility(View.VISIBLE);
                quantityDisplay.setVisibility(View.VISIBLE);
            }
            break;*/
        }
    }
}
