package com.zconnect.mondiner.customer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.mondiner.customer.adapters.OffersAdapter;
import com.zconnect.mondiner.customer.models.Offers;
import com.zconnect.mondiner.customer.utils.Details;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OffersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OffersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OffersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView offersrv;
    private TextView noItemText;
    private ArrayList<Offers> offersArrayList = new ArrayList<>();
    private OffersAdapter offersAdapter;
    private DatabaseReference mOffersRef;
    private ValueEventListener mOfferListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public OffersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OffersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OffersFragment newInstance(String param1, String param2) {
        OffersFragment fragment = new OffersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_offers, container, false);
        offersrv = rootView.findViewById(R.id.alloffers_rv);
        noItemText  =rootView.findViewById(R.id.no_item_offer_text);
        mOffersRef = FirebaseDatabase.getInstance().getReference().child("restaurants");
        offersrv.setLayoutManager(new LinearLayoutManager(getContext()));
        offersAdapter = new OffersAdapter(offersArrayList);
        offersrv.setAdapter(offersAdapter);
        mOfferListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offersArrayList.clear();
                for(DataSnapshot restaurants : dataSnapshot.getChildren()){
                    for(DataSnapshot offers : restaurants.child("offers").getChildren()){
                        Log.e("OffersActivity","Datasnapshot" + dataSnapshot.toString());
                        Offers o = new Offers();
                        o.setOfferName(offers.child("title").getValue(String.class));
                        o.setDescription(offers.child("description").getValue(String.class));
                        o.setImageUri(offers.child("imageURL").getValue(String.class));
                        offersArrayList.add(o);
                    }
                }
                offersrv.setAdapter(offersAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mOffersRef.addValueEventListener(mOfferListener);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Toast.makeText(context, "Offers Fragment Attached", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mOffersRef.removeEventListener(mOfferListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mOffersRef.addValueEventListener(mOfferListener);
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
