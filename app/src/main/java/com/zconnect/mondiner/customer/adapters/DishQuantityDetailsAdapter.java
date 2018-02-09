package com.zconnect.mondiner.customer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.CartUserData;
import com.zconnect.mondiner.customer.viewholders.QuantityDetailsViewholder;

import java.util.ArrayList;

/**
 * Created by Ishaan on 29-01-2018.
 */

public class DishQuantityDetailsAdapter extends RecyclerView.Adapter<QuantityDetailsViewholder>{

    private ArrayList<CartUserData> userData;

    public DishQuantityDetailsAdapter(ArrayList<CartUserData> cartUserData){
        this.userData = cartUserData;
    }

    @Override
    public QuantityDetailsViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_quantity_details, parent, false);
        return new QuantityDetailsViewholder(newView);
    }

    @Override
    public void onBindViewHolder(QuantityDetailsViewholder holder, int position) {
        holder.populate(userData.get(position));
    }

    @Override
    public int getItemCount() {
        return this.userData.size();
    }

    /*@Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }*/
}
