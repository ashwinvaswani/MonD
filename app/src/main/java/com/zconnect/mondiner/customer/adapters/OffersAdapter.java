package com.zconnect.mondiner.customer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.Offers;
import com.zconnect.mondiner.customer.viewholders.OffersViewholder;

import java.util.ArrayList;

/**
 * Created by grandiose on 17/2/18.
 */

public class OffersAdapter extends RecyclerView.Adapter<OffersViewholder>{
    private ArrayList<Offers> offers;

    public OffersAdapter(ArrayList<Offers> offers){
        this.offers = offers;
    }

    @Override
    public OffersViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_offers, parent, false);
        OffersViewholder offersViewholder = new OffersViewholder(newView);
        return offersViewholder;
    }

    @Override
    public void onBindViewHolder(OffersViewholder holder, int position) {
        holder.populate(offers.get(position));
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }
}
