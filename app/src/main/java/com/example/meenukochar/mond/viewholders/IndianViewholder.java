package com.example.meenukochar.mond.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.meenukochar.mond.R;

/**
 * Created by Ishaan on 11-01-2018.
 */

public class IndianViewholder extends RecyclerView.ViewHolder {

    public TextView itemNametv;
    public TextView itemPricetv;
    public TextView itemVegtv;

    public IndianViewholder(View itemView) {

        super(itemView);

        itemNametv = itemView.findViewById(R.id.item_name_indian);
        itemPricetv = itemView.findViewById(R.id.item_price_indian);
        itemVegtv = itemView.findViewById(R.id.item_veg_indian);

    }
}
