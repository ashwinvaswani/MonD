package com.zconnect.mondiner.customer.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zconnect.mondiner.customer.R;

/**
 * Created by Ishaan on 26-01-2018.
 */

public class CartUserConfirmationViewholder extends RecyclerView.ViewHolder {

    public TextView userName;
    public TextView userStatus;

    public CartUserConfirmationViewholder(View itemView){
        super(itemView);
        userName = itemView.findViewById(R.id.user_name);
        userStatus = itemView.findViewById(R.id.user_status);
    }
}
