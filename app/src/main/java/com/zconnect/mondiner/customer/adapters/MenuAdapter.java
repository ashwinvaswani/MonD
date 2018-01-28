package com.zconnect.mondiner.customer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.Menu;
import com.zconnect.mondiner.customer.viewholders.MenuViewHolder;

import java.util.ArrayList;

/**
 * Created by Ishaan.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder> {

    private ArrayList<Menu> menus;
    private ArrayList<String> dishIDs = new ArrayList<>();

    public MenuAdapter(ArrayList<Menu> menuArray, ArrayList<String> dishIDs) {
        menus = menuArray;
        this.dishIDs = dishIDs;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(newView);
    }


    @Override
    public void onBindViewHolder(final MenuViewHolder holder, final int position) {
        holder.populate(menus.get(position), dishIDs.get(position));
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }
}

