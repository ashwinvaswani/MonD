package com.zconnect.mondiner.customer.viewholders;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.DishQuantityDetails;
import com.zconnect.mondiner.customer.utils.Details;

/**
 * Created by Ishaan on 21-01-2018.
 */

public class CartViewholder extends RecyclerView.ViewHolder{

    public TextView itemNametv;
    public TextView itemPricetv;
    public TextView itemQuantitytv;
    public TextView itemAmounttv;
    private DatabaseReference userDetails;
  //  public ListView usersList;

    public CartViewholder(View itemView){
        super(itemView);

        itemNametv = itemView.findViewById(R.id.item_name);
        itemPricetv = itemView.findViewById(R.id.item_price);
        itemQuantitytv = itemView.findViewById(R.id.item_quantity);
        itemAmounttv = itemView.findViewById(R.id.item_amount);

    //    usersList = itemView.findViewById(R.id.users_list);

    }

    public void openItem(final String dishID){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Log.e("CartViewholder",""+dishID);
                Details.DISH_ID = dishID;
                intent.setClass(itemView.getContext(), DishQuantityDetails.class).putExtra(dishID, "dishID");
                ((Activity) itemView.getContext()).startActivity(intent);
            }
        });
    }
}
