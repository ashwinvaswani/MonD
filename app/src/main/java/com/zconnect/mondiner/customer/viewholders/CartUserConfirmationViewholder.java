package com.zconnect.mondiner.customer.viewholders;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.CartUserData;

/**
 * Created by Ishaan on 26-01-2018.
 */

public class CartUserConfirmationViewholder extends RecyclerView.ViewHolder {

    public TextView userName;
    public SimpleDraweeView userStatus;
    public SimpleDraweeView userChecked;

    public CartUserConfirmationViewholder(View itemView){
        super(itemView);
        userName = itemView.findViewById(R.id.user_name);
        userStatus = itemView.findViewById(R.id.user_image);
        userChecked = itemView.findViewById(R.id.user_status_check);
    }

    public void populate(final CartUserData cartUserData) {
        userStatus.setVisibility(View.VISIBLE);
        userName.setText(cartUserData.getUserName());
        if(cartUserData.getUserStatus().equalsIgnoreCase("yes")){
            userChecked.setVisibility(View.VISIBLE);
            userStatus.setVisibility(View.GONE);
        }
        else if(cartUserData.getUserStatus().equalsIgnoreCase("no")){
            userChecked.setVisibility(View.GONE);
            userStatus.setVisibility(View.VISIBLE);
        }
    }
}
