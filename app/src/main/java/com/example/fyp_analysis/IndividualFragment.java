package com.example.fyp_analysis;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.core.Tag;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IndividualFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndividualFragment extends Fragment implements MyAdapter.OnItemListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mrecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mlayoutManager;

    private FirebaseDatabase firebaseDatabase;
    private ArrayList<String>newUserProfiles;
    private ArrayList<String>newUserID;

    private TextView tv;

    public IndividualFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IndividualFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IndividualFragment newInstance(String param1, String param2) {
        IndividualFragment fragment = new IndividualFragment();
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
        View v = inflater.inflate(R.layout.fragment_individual, container, false);
        mrecyclerView = v.findViewById(R.id.user_recycler_view);
        tv=v.findViewById(R.id.textView2);
        getUserName();
        InsertRecyclerView();

        // Inflate the layout for this fragment
        return v;
    }

    public void getUserName(){
        newUserProfiles = new ArrayList<>();
        newUserID = new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Users/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                        UserProfile userProfile = myDataSnapshot.getValue(UserProfile.class);
                        newUserProfiles.add(userProfile.getUserName());
                        newUserID.add(myDataSnapshot.getKey());
                       //tv.setText(dataSnapshot.getKey());
                    }
                    mAdapter.notifyDataSetChanged();
                    //InsertRecyclerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void InsertRecyclerView() {
        mlayoutManager = new LinearLayoutManager(getContext());
        mrecyclerView.setLayoutManager(mlayoutManager);
        mrecyclerView.setHasFixedSize(true);
        mAdapter = new MyAdapter(newUserID, newUserProfiles,this);
        mrecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void OnItemClick(int position) {
        //navigate to new activity
        //newUserID.get(position);
        Intent intent=new Intent(getActivity(), IndividualUserDetails.class);
        intent.putExtra("UserID", newUserID.get(position));
        startActivity(intent);
        Log.d("RECYLCERVIEW","CLICKED" + position);
        //Put the value

//        IndividualUserDetails IUD = new IndividualUserDetails();
//        Bundle args = new Bundle();
//        args.putString("UserID", newUserID.get(position));
//        IUD.setArguments(args);
//        getActivity().finish();
////Inflate the fragment
//        getFragmentManager().beginTransaction().add(R.id.container, IUD).commit();
    }
}
