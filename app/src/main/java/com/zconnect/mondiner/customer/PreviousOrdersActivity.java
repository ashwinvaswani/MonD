package com.zconnect.mondiner.customer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class PreviousOrdersActivity extends AppCompatActivity {

    private RecyclerView prevOrdersrv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_orders);
        prevOrdersrv = findViewById(R.id.prev_orders_rv);


    }
}
