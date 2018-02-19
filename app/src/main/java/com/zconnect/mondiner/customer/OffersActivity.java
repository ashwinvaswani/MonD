package com.zconnect.mondiner.customer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.mondiner.customer.adapters.OffersAdapter;
import com.zconnect.mondiner.customer.models.Offers;
import com.zconnect.mondiner.customer.utils.Details;

import java.util.ArrayList;

public class OffersActivity extends AppCompatActivity {

    private DatabaseReference mOffersRef;
    private ValueEventListener mOfferListener;
    private ArrayList<Offers> offersArrayList = new ArrayList<>();
    private RecyclerView offersRv;
    private OffersAdapter offersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        offersRv = findViewById(R.id.offers_rv);
        mOffersRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(Details.REST_ID).child("offers");
        offersRv.setLayoutManager(new LinearLayoutManager(this));
        offersAdapter = new OffersAdapter(offersArrayList);
        mOfferListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offersArrayList.clear();
                for(DataSnapshot offers : dataSnapshot.getChildren()){
                    Log.e("OffersActivity","Datasnapshot" + dataSnapshot.toString());
                    Offers o = new Offers();
                    o.setOfferName(offers.child("title").getValue(String.class));
                    o.setDescription(offers.child("description").getValue(String.class));
                    o.setImageUri(offers.child("imageURL").getValue(String.class));
                    offersArrayList.add(o);
                }
                offersRv.setAdapter(offersAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mOffersRef.addValueEventListener(mOfferListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOffersRef.removeEventListener(mOfferListener);
    }
}

