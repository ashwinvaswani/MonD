package com.zconnect.mondiner.customer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.Menu;
import com.zconnect.mondiner.customer.viewholders.AddOnViewHolder;

import java.util.ArrayList;

/**
 * Created by grandiose on 15/3/18.
 */

public class AddOnAdapter extends RecyclerView.Adapter<AddOnViewHolder> {
    private ArrayList<Menu> addOns;
    private ArrayList<String> dishIDs = new ArrayList<>();
    private Context context;
    private String rsSymbol;
    public AddOnAdapter(ArrayList<Menu> addOnsArray, ArrayList<String> dishIDs, String rsSymbol, Context context){
        this.addOns = addOnsArray;
        this.dishIDs = dishIDs;
        this.context = context;
        this.rsSymbol = rsSymbol;
    }

    @NonNull
    @Override
    public AddOnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_add_on, parent, false);
        return new AddOnViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddOnViewHolder holder, int position) {
        holder.populate(addOns.get(position), dishIDs.get(position), rsSymbol, context);
    }

    @Override
    public int getItemCount() {
        return addOns.size();
    }
}
