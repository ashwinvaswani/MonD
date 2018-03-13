package com.zconnect.mondiner.customer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 1000;
    private final int RC_PERM_REQ_EXT_STORAGE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if(checkPermission()) {
                    Intent i = new Intent(SplashActivity.this, TabbedMenu.class);
                    startActivity(i);

                    // close this activity
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SplashActivity.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Permission to read storage is required.");
            alertBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 7);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERM_REQ_EXT_STORAGE);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PERM_REQ_EXT_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(SplashActivity.this, TabbedMenu.class));
                finish();
            } else {
                Toast.makeText(this, "Permission Denied !, Retrying.", Toast.LENGTH_SHORT).show();
                checkPermission();
            }
        }
    }
}

