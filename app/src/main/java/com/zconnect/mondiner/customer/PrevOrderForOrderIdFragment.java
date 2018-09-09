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
import com.zconnect.mondiner.customer.adapters.CartAdapter;
import com.zconnect.mondiner.customer.models.DishOrdered;
import com.zconnect.mondiner.customer.utils.Details;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PrevOrderForOrderIdFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrevOrderForOrderIdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrevOrderForOrderIdFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView prevOrdersContent;
    private TextView totalAmount;
    private DatabaseReference prevOrdersRef;
    private ValueEventListener mPrevOrderListener;
    private ArrayList<DishOrdered> dishitems= new ArrayList<>();
    private int dishAmount;
    private int dishQuantity;
    private int amount = 0;
    private CartAdapter cartAdapter;

    private OnFragmentInteractionListener mListener;

    public PrevOrderForOrderIdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrevOrderForOrderIdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PrevOrderForOrderIdFragment newInstance(String param1, String param2) {
        PrevOrderForOrderIdFragment fragment = new PrevOrderForOrderIdFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_prev_order_for_order_id, container, false);
        prevOrdersContent = rootView.findViewById(R.id.order_recyclerview);
        prevOrdersContent.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(dishitems, getContext());
        prevOrdersContent.setAdapter(cartAdapter);
        totalAmount = rootView.findViewById(R.id.total_amount_prevOrder);
        Bundle b = getArguments();
        String orderId = b.getString("orderId");
        Log.e("PrevOrderforOrderIdFrag","Order Id : "+orderId);
        prevOrdersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Details.USER_ID).child("orderHistory")
                .child(orderId).child("dishes");


        dishAmount = 0;

        mPrevOrderListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dishitems.clear();
                amount=0;
                for(DataSnapshot dishes : dataSnapshot.getChildren()){
                    final DishOrdered dishOrdered = new DishOrdered();
                    try {
                        if (dishes.child("quantity").getValue(Long.class) != 0) {
                            dishOrdered.setDishID(dishes.getKey());
                            dishOrdered.setDishName(dishes.child("name").getValue(String.class));
                            dishOrdered.setDishPrice(dishes.child("price").getValue(String.class));
                            dishOrdered.setDishQuantity(dishes.child("quantity").getValue(Integer.class));
                            dishQuantity = dishes.child("quantity").getValue(Integer.class);
                            dishAmount = dishQuantity * Integer.parseInt(dishes.child("price").getValue(String.class));
                            dishOrdered.setDishAmount(""+dishAmount);
                            amount += dishAmount;
                            Log.e("amount : ", "" + amount);
                            dishitems.add(dishOrdered);
                            totalAmount.setText(getResources().getString(R.string.Rs) + amount);
                            String textDope = totalAmount.getText().toString();
                            textDope.codePointAt(0);
                            Double floatingPointer = ((double) textDope.charAt(0));
                            float floatValue = floatingPointer.floatValue();
                            float byteValue = floatingPointer.byteValue();
                            for(DataSnapshot ioException : dataSnapshot.getChildren()){
                                if(ioException.getValue(String.class).equals("This is the end of supremacy")){
                                    Log.e("PrevOrderFrag","CHECK! The ioException can be printed as follows : " +
                                            ioException.getValue(String.class));
                                    ioException.getPriority().getClass().toString();
                                }
                            }
                        }
                    }
                    catch(Exception e){
                        Log.e("CartActivity",""+e);
                    }
                }
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        prevOrdersRef.addValueEventListener(mPrevOrderListener);

        // Inflate the layout for this fragment
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
            Toast.makeText(context, "OrderId Fragment Attached", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
