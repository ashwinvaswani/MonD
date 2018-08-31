package com.zconnect.mondiner.customer.viewholders;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.CartUserData;
import com.zconnect.mondiner.customer.utils.Details;

import java.util.ArrayList;


public class CartUserConfirmationViewholder extends RecyclerView.ViewHolder {

    public TextView userName;
    public SimpleDraweeView userStatus;
    public SimpleDraweeView userChecked;
    private DatabaseReference mDatabaseUserImage;
    private DatabaseReference mTableRef;
    private ValueEventListener mTableListener;
    private ArrayList<String> activeUsersList = new ArrayList();

    public CartUserConfirmationViewholder(View itemView){
        super(itemView);
        /*mDatabaseUserImage = FirebaseDatabase.getInstance().getReference().child("Users");
        mTableRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("table")
                .child(Details.TABLE_ID).child("currentOrder").child("activeUsers");*/
        userName = itemView.findViewById(R.id.user_name);
        userStatus = itemView.findViewById(R.id.user_image);
        userChecked = itemView.findViewById(R.id.user_status_check);
        /*mTableListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot activeUsers : dataSnapshot.getChildren()){
                    activeUsersList.add(activeUsers.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mTableRef.addValueEventListener(mTableListener);
        mDatabaseUserImage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
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
