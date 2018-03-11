package com.zconnect.mondiner.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.zconnect.mondiner.customer.utils.Details;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.hardware.camera2.CaptureResult.FLASH_STATE;
import static com.google.zxing.client.android.Intents.Scan.CAMERA_ID;


public class QRFragment extends Fragment implements ZXingScannerView.ResultHandler{

    private String contents;
    private ZXingScannerView mScannerView;
    DecoratedBarcodeView decoratedBarcodeView;
    private CaptureManager capture;

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private boolean mFlash;
    private boolean mAutoFocus;
    private int mCameraId = -1;
    private ArrayList<Integer> mSelectedIndices;

    private ValueEventListener restListener;

    private OnFragmentInteractionListener mListener;

    private DatabaseReference mRefRestID;
    private SharedPreferences preferences;

    public QRFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle state) {
        // Inflate the layout for this fragment
        Log.e("QRFragment","in onCreateView");
        mRefRestID = FirebaseDatabase.getInstance().getReference().child("restaurants");
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        mScannerView = new ZXingScannerView(getActivity());
        if(state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }
        setupFormats();
        Log.e("QRFragment","after setupFormats");


        return mScannerView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public void handleResult(Result result) {
        Toast.makeText(getContext(), "" + result.getText(), Toast.LENGTH_SHORT).show();
        if (result.getText().toString() == null) {
            Toast.makeText(getContext(), "QR not scanned", Toast.LENGTH_LONG).show();
        } else {
            try {//Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Log.e("QrActivity", "QR Scanned = " + result.getText());
                String information[] = result.getText().toString().split(";");
                String RestID = information[0].trim();
                String TableID = information[1].trim();
                preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("restaurantId", RestID);
                editor.putString("currentTableId", TableID);
                editor.apply();
                Log.e("QrActivity", "Table : " + information[1].trim());
                checkRestId(RestID, TableID);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Data cannot be processed", Toast.LENGTH_SHORT).show();
            }
            //info 0 RestID and info 1 has TableID
        }
    }

    private void checkRestId(final String restID, final String tableID) {
        restListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    if (childSnapShot.getKey().equals(restID)) {
                        Log.e("QrActivity", "Rest ID found" + restID);
                        //      Toast.makeText(QR_Offers_prevOrders.this, "The RestID was equal!", Toast.LENGTH_SHORT).show();
                        checkTableID(childSnapShot, tableID, restID);
                    } else {
                        continue;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRefRestID.addValueEventListener(restListener);
    }

    private void checkTableID(DataSnapshot childSnapShot, String tableID, String restID) {
        Toast.makeText(getContext(), "Clear TOp", Toast.LENGTH_SHORT).show();
        for (DataSnapshot grandChildSnapShot : childSnapShot.child("table").getChildren()) {
            if (grandChildSnapShot.getKey().equals(tableID)) {
                preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String restaurantId = preferences.getString("restaurantId", "");
                String currentTableId = preferences.getString("currentTableId","");
                Details.REST_ID = restaurantId;
                Details.TABLE_ID = currentTableId;
                Log.e("QRActivity","Checking shared preferences : " + Details.REST_ID + "--" + Details.TABLE_ID);
                mRefRestID.child(Details.REST_ID).child("table").child(Details.TABLE_ID).child("availability").setValue("false");
                Intent tomain = new Intent(getActivity(), TabbedMenu.class);
                tomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Toast.makeText(getContext(), "Please select your dishes...", Toast.LENGTH_SHORT).show();
                mRefRestID.removeEventListener(restListener);
                startActivity(tomain);
            } else {
                continue;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        decoratedBarcodeView.pauseAndWait();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Toast.makeText(context, "QRFragment Attached", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
