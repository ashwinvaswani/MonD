package com.zconnect.mondiner.customer.viewholders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.CartUserData;
import com.zconnect.mondiner.customer.utils.Details;

/**
 * Created by Ishaan on 29-01-2018.
 */

public class QuantityDetailsViewholder extends RecyclerView.ViewHolder {
    private TextView userName;
    private TextView userQuantity;
    private Button increment;
    private Button decrement;
    private CartUserData cartUserData;
    private DatabaseReference mTableRef;

    public QuantityDetailsViewholder(View itemView){
        super(itemView);
        userName = itemView.findViewById(R.id.user_name);
        userQuantity = itemView.findViewById(R.id.user_status);
        increment = itemView.findViewById(R.id.increment_button_user);
        decrement = itemView.findViewById(R.id.decrement_button_user);
        this.mTableRef = FirebaseDatabase.getInstance().getReference()
                .child("restaurants")
                .child(Details.REST_ID)
                .child("table")
                .child(Details.TABLE_ID)
                .child("currentOrder")
                .child("cart");
    }
    public void populate(final CartUserData userData) {
        this.cartUserData = userData;
        userName.setText(userData.getUserName());
        userQuantity.setText(userData.getUserQuantity());
        this.increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                Log.e("MenuViewHolder" +
                        " ", "in textview" + userQuantity.getText().toString());
                i = Integer.parseInt(cartUserData.getUserQuantity()) + 1;
                cartUserData.setUserQuantity(i + "");
                mTableRef.child(Details.DISH_ID).child("users").child(Details.USER_ID).setValue(cartUserData.getUserQuantity());
            }
        });
        this.decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                i = Integer.parseInt(cartUserData.getUserQuantity()) - 1;
                if (i <= 0) {
                    mTableRef.child(Details.DISH_ID).child("users").child(Details.USER_ID).setValue("0");
                    userQuantity.setText("0");
                } else {
                    i = Integer.parseInt(cartUserData.getUserQuantity()) - 1;
                    cartUserData.setUserQuantity(i + "");
                    mTableRef.child(Details.DISH_ID).child("users").child(Details.USER_ID).setValue(cartUserData.getUserQuantity());
                }
            }
        });
    }
}
