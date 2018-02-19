package com.zconnect.mondiner.customer.utils;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by grandiose on 17/2/18.
 */

public class FirebasePersistence {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
