package com.zconnect.mondiner.customer.viewholders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zconnect.mondiner.customer.PrevOrderForOrderIdFragment;
import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.PrevOrders;

public class PrevOrderViewholder extends RecyclerView.ViewHolder{

    private TextView orderRestaurant;
    private TextView orderDate;
    private TextView orderAmount;
    private TextView orderId;
    private Context context;

    public PrevOrderViewholder(View itemView){
        super(itemView);
        orderRestaurant = itemView.findViewById(R.id.order_restaurant);
        orderAmount = itemView.findViewById(R.id.order_amount);
        orderDate = itemView.findViewById(R.id.order_date);
        orderId = itemView.findViewById(R.id.order_id);
    }

    public void populate(PrevOrders prevOrders, final String rsSymbol) {
        orderId.setText("Order ID : "+prevOrders.getOrderId());
        orderRestaurant.setText(prevOrders.getRestaurantName());
        orderDate.setText(prevOrders.getDate());
        orderAmount.setText(rsSymbol+prevOrders.getTotalAmount());
    }

    public void openItem(final String orderId, final Context context){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                PrevOrderForOrderIdFragment prevOrderForOrderIdFragment = new PrevOrderForOrderIdFragment();
                Bundle orderIdBundle = new Bundle();
                orderIdBundle.putString("orderId",orderId);
                prevOrderForOrderIdFragment.setArguments(orderIdBundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, prevOrderForOrderIdFragment).commit();
            }
        });
    }
}
