package com.example.meenukochar.mond;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Ishaan on 09-01-2018.
 */

public class Continental_tab extends Fragment {

    RecyclerView continentalMenu;
    TextView continentalText;
    //ArrayList<Menu> menus = new ArrayList<Menu>();

    DatabaseReference mMenuRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.continental_tab, container, false);

        continentalMenu = rootView.findViewById(R.id.continental_menu_rv);
        continentalText = rootView.findViewById(R.id.continental_text);

        return rootView;
    }

}
