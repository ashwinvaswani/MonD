package com.zconnect.mondiner.customer.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.Menu;
import com.zconnect.mondiner.customer.viewholders.IndianViewholder;

/**
 * Created by Ishaan on 11-01-2018.
 */

public class MenuAdapter extends RecyclerView.Adapter<IndianViewholder> {

    Menu[] menus;

    public MenuAdapter(Menu[] menuArray) {

        menus=menuArray;

    }

    @Override
    public IndianViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View newView=layoutInflater.inflate(R.layout.item_indian_menu,parent,false);
        IndianViewholder indianViewholder=new IndianViewholder(newView);
        Log.e("uptou","oncreate called");
        return indianViewholder;
    }


    @Override
    public int getItemCount() {
        return menus.length;
    }

    @Override
    public void onBindViewHolder(IndianViewholder holder, int position) {
        Log.e("uptou","onbind called at"+position);
        holder.itemNametv.setText(menus[position].getItemName());
        String price = Double.toString(menus[position].getItemPrice());
        holder.itemPricetv.setText(price);
        if(menus[position].getVegNonVeg()==1){
            holder.itemVegtv.setText("Veg");
        }
        else{
            holder.itemVegtv.setText("Non-Veg");
        }

    }
}

