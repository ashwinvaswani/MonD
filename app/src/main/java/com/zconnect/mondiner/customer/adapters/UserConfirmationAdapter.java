package com.zconnect.mondiner.customer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.CartUserData;
import com.zconnect.mondiner.customer.viewholders.CartUserConfirmationViewholder;

import java.util.ArrayList;

/**
 * Created by Ishaan on 26-01-2018.
 */

public class UserConfirmationAdapter extends RecyclerView.Adapter<CartUserConfirmationViewholder> {

    private ArrayList<CartUserData> userCartStatus;

    public UserConfirmationAdapter(ArrayList<CartUserData> cartUserData){
        this.userCartStatus = cartUserData;
    }

    @Override
    public CartUserConfirmationViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View newView = layoutInflater.inflate(R.layout.item_cart_user_confirm, parent, false);
        return new CartUserConfirmationViewholder(newView);
    }

    @Override
    public void onBindViewHolder(CartUserConfirmationViewholder holder, int position) {
        holder.populate(userCartStatus.get(position));
    }

    @Override
    public int getItemCount() {
        return this.userCartStatus.size();
    }

    /*@Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }*/
}
