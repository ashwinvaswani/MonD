package com.zconnect.mondiner.customer.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.DishOrdered;
import com.zconnect.mondiner.customer.viewholders.CartViewholder;

import java.util.ArrayList;

/**
 * Created by Ishaan on 21-01-2018.
 */

public class CartAdapter extends RecyclerView.Adapter<CartViewholder> {

    private ArrayList<DishOrdered> dishitems;


    public CartAdapter(ArrayList<DishOrdered> dishArray)    {
        dishitems = dishArray;
    }

    @Override
    public CartViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_cart, parent, false);
        CartViewholder cartViewholder = new CartViewholder(newView);
        Log.e("uptou", "oncreate called");
        return cartViewholder;
    }

    public void onBindViewHolder(final CartViewholder holder, final int position) {
        holder.itemNametv.setText(dishitems.get(position).getDishName());
        holder.itemQuantitytv.setText(dishitems.get(position).getDishQuantity());
        holder.itemPricetv.setText(dishitems.get(position).getDishPrice());
        holder.itemAmounttv.setText(dishitems.get(position).getDishAmount());
        //int amount = Integer.parseInt(dishitems.get(position).getDishPrice())*Integer.parseInt(dishitems.get(position).getDishQuantity());
        //holder.itemAmounttv.setText(amount+"");
    }

    @Override
    public int getItemCount() {
        return dishitems.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
