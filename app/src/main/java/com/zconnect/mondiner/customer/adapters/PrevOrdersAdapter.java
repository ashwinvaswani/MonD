package com.zconnect.mondiner.customer.adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.PrevOrders;
import com.zconnect.mondiner.customer.viewholders.PrevOrderViewholder;

import java.util.ArrayList;

/**
 * Created by Ishaan on 03-02-2018.
 */

public class PrevOrdersAdapter extends RecyclerView.Adapter<PrevOrderViewholder>{

    private ArrayList<PrevOrders> prevOrders;
    private Context context;
    private String rsSymbol;

    public PrevOrdersAdapter(ArrayList<PrevOrders> prevOrders, final String rsSymbol, Context context){
        this.prevOrders = prevOrders;
        this.context = context;
        this.rsSymbol = rsSymbol;
    }

    @NonNull
    @Override
    public PrevOrderViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_prev_orders, parent, false);
        PrevOrderViewholder prevOrderViewholder = new PrevOrderViewholder(newView);
        return prevOrderViewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull PrevOrderViewholder holder, int position) {
        holder.populate(prevOrders.get(position), rsSymbol);
        holder.openItem(prevOrders.get(position).getOrderId(), context);
    }

    @Override
    public int getItemCount() {
        return prevOrders.size();
    }
}
