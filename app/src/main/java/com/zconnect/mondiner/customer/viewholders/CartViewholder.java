package com.zconnect.mondiner.customer.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zconnect.mondiner.customer.R;

/**
 * Created by Ishaan on 21-01-2018.
 */

public class CartViewholder extends RecyclerView.ViewHolder{

    public TextView itemNametv;
    public TextView itemPricetv;
    public TextView itemQuantitytv;
    public TextView itemAmounttv;
  //  public ListView usersList;

    public CartViewholder(View itemView){
        super(itemView);

        itemNametv = itemView.findViewById(R.id.item_name);
        itemPricetv = itemView.findViewById(R.id.item_price);
        itemQuantitytv = itemView.findViewById(R.id.item_quantity);
        itemAmounttv = itemView.findViewById(R.id.item_amount);
    //    usersList = itemView.findViewById(R.id.users_list);
    }
}
