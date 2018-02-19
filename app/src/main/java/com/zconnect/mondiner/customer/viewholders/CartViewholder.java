package com.zconnect.mondiner.customer.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.DishQuantityDetails;
import com.zconnect.mondiner.customer.models.DishOrdered;
import com.zconnect.mondiner.customer.utils.Details;

/**
 * Created on 21-01-2018.
 */

public class CartViewholder extends RecyclerView.ViewHolder {

    public TextView itemNametv;
    public TextView itemPricetv;
    public TextView itemQuantitytv;
    public TextView itemAmounttv;
    private Button decrement;
    private Button increment;
    private DishOrdered dishOrdered;
    private DatabaseReference userDetails;
    private DatabaseReference mTableRef;
    //  public ListView usersList;

    public CartViewholder(View itemView) {
        super(itemView);

        itemNametv = itemView.findViewById(R.id.item_name);
        itemPricetv = itemView.findViewById(R.id.item_price);
        itemQuantitytv = itemView.findViewById(R.id.item_quantity);
        itemAmounttv = itemView.findViewById(R.id.item_amount);
        /*increment = itemView.findViewById(R.id.increment_button);
        decrement = itemView.findViewById(R.id.decrement_button);*/
        //    usersList = itemView.findViewById(R.id.users_list);
        this.mTableRef = FirebaseDatabase.getInstance().getReference()
                .child("restaurants")
                .child(Details.REST_ID)
                .child("table")
                .child(Details.TABLE_ID)
                .child("currentOrder")
                .child("cart");

    }



    public void populate(DishOrdered dishitems, Context context) {
        this.dishOrdered = dishitems;
        itemNametv.setText(dishitems.getDishName());
        itemQuantitytv.setText(dishitems.getDishQuantity());
        itemPricetv.setText(context.getResources().getString(R.string.Rs)+dishitems.getDishPrice());
        itemAmounttv.setText(context.getResources().getString(R.string.Rs)+dishitems.getDishAmount());

    }


//
    public void openItem(final String dishID) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Log.e("CartViewholder", "" + dishID);
                Details.DISH_ID = dishID;
                intent.setClass(itemView.getContext(), DishQuantityDetails.class).putExtra(dishID, "dishID");
                ((Activity) itemView.getContext()).startActivity(intent);
            }
        });


    }


}
