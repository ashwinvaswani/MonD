package com.zconnect.mondiner.customer.viewholders;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.mondiner.customer.R;
import com.zconnect.mondiner.customer.models.Offers;

/**
 * Created by grandiose on 17/2/18.
 */

public class OffersViewholder extends RecyclerView.ViewHolder{
    public TextView offerTitletv;
    public TextView offerDescriptiontv;
    public SimpleDraweeView offerImage;
    private Offers offers;

    public OffersViewholder(View itemView){
        super(itemView);
        offerTitletv = itemView.findViewById(R.id.offer_title);
        offerDescriptiontv = itemView.findViewById(R.id.offer_information);
        offerImage = itemView.findViewById(R.id.offer_image);
    }

    public void populate(Offers offers){
        this.offers = offers;
        offerTitletv.setText(offers.getOfferName());
        offerDescriptiontv.setText(offers.getDescription());
        if(!offers.getImageUri().equalsIgnoreCase("")) {
            Uri uri = Uri.parse(offers.getImageUri());
            offerImage.setImageURI(uri);
        }
    }
}
