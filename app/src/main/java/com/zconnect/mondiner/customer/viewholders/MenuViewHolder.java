package com.zconnect.mondiner.customer.viewholders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.Menu;
import com.zconnect.mondiner.customer.utils.Details;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView itemNametv;
    private TextView itemPricetv;
    private TextView itemVegtv;
    private Button decrement;
    private Button increment;
    private TextView quantityDisplay;
    private final DatabaseReference mTableRef;
    private Menu menu;
    private String dishId;

    public MenuViewHolder(View itemView) {
        super(itemView);
        itemNametv = itemView.findViewById(R.id.item_name_indian);
        itemPricetv = itemView.findViewById(R.id.item_price_indian);
        itemVegtv = itemView.findViewById(R.id.item_veg_indian);
        decrement = itemView.findViewById(R.id.decrement_button);
        increment = itemView.findViewById(R.id.increment_button);
        quantityDisplay = itemView.findViewById(R.id.quantity_display);

        this.mTableRef = FirebaseDatabase.getInstance().getReference()
                .child("restaurants")
                .child(Details.REST_ID)
                .child("table")
                .child(Details.TABLE_ID)
                .child("currentOrder")
                .child("cart");
                //.child("dishes");
    }

    public void populate(final com.zconnect.mondiner.customer.models.Menu menu, final String dishId) {
        this.menu = menu;
        this.dishId = dishId;
        itemNametv.setText(menu.getItemName());
        itemPricetv.setText(menu.getItemPrice());
        itemVegtv.setText(menu.getVegNonVeg());
        quantityDisplay.setText(menu.getItemQuantity());
        increment.setOnClickListener(this);
        decrement.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.increment_button: {
                Log.e("MenuViewHolder" +
                        " ", "in textview" + quantityDisplay.getText().toString());
                int i = Integer.parseInt(menu.getItemQuantity()) + 1;
                menu.setItemQuantity(i + "");
                mTableRef.child(dishId).child("price").setValue(menu.getItemPrice());
                mTableRef.child(dishId).child("name").setValue(menu.getItemName());
                mTableRef.child(dishId).child("users").child(Details.USER_ID).setValue(menu.getItemQuantity());
                break;
            }
            case R.id.decrement_button: {
                if(Integer.parseInt(quantityDisplay.getText().toString())<0){
                    mTableRef.child(dishId).child("users").child(Details.USER_ID).setValue("0");
                    quantityDisplay.setText("0");
                /*if (Integer.parseInt(menu.getItemQuantity()) < 0) {
                    mTableRef.child(dishId).child("users").child(Details.USER_ID).setValue("0");
                    menu.setItemQuantity("0");*/
                }
                else {
                    int i = Integer.parseInt(menu.getItemQuantity()) - 1;
                    mTableRef.child(dishId).child(Details.USER_ID).child("quantity").setValue(i + "");
                    menu.setItemQuantity(i + "");
                    mTableRef.child(dishId).child("users").child(Details.USER_ID).setValue(menu.getItemQuantity());
                }
                break;
            }
        }
    }
}
