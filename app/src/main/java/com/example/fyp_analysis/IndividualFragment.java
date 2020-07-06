package com.example.fyp_analysis;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


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
    private String user, userID="";

    private RecyclerView mrecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mlayoutManager;

    private FirebaseDatabase firebaseDatabase;
    private ArrayList<String>newUserProfiles;
    private ArrayList<String>newUserID;
    private ArrayList<String>newUserAge;
    private ArrayList<String>newUserEmail;
    private ArrayList<String>newUserHeight;
    private ArrayList<String>newUserWeight;
    private ArrayList<String>newGroup;
    private ArrayList<String>newUserGender;
    private ArrayList<String>newDate;
    private ArrayList<String>userSteps;
    private ArrayList<String>userStepsID;
    private ArrayList<ArrayList<String>> mainArray ;



    private TextView tv;

    private Button exportData, exportActivities;

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
        exportData=v.findViewById(R.id.Export_everything);
        exportActivities=v.findViewById(R.id.btnExportActivities);
        firebaseDatabase= FirebaseDatabase.getInstance();
        getUserName();
        getSteps();
        InsertRecyclerView();
        exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getUserProfile();
                export();
            }
        });
        exportActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportActivityData();
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    public void getUserName(){
        //firebaseDatabase=FirebaseDatabase.getInstance();
        newUserProfiles = new ArrayList<>();
        newUserID = new ArrayList<>();
        newUserAge = new ArrayList<>();
        newUserEmail = new ArrayList<>();
        newUserHeight = new ArrayList<>();
        newUserWeight = new ArrayList<>();
        newGroup = new ArrayList<>();
        newUserGender=new ArrayList<>();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Users/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                        UserProfile userProfile = myDataSnapshot.getValue(UserProfile.class);
                        newUserProfiles.add(userProfile.getUserName());
                        newUserID.add(myDataSnapshot.getKey());
                        newUserAge.add(userProfile.getUserAge());
                        newUserEmail.add(userProfile.getUserEmail());
                        newUserHeight.add(userProfile.getUserHeight());
                        newUserWeight.add(userProfile.getUserWeight());
                        newUserGender.add(userProfile.getUserGender());
                        if(userProfile.getGroup()!=null)
                            newGroup.add(userProfile.getGroup());
                        else
                            newGroup.add(null);
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

    private void export() {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Users,Name,Age,Email,Gender,Height,Weight,Group"); //,Group
        for (int i = 0; i < newUserID.size(); i++) {
            data.append("\n" + newUserID.get(i) + "," + newUserProfiles.get(i) + "," + newUserAge.get(i) + "," + newUserEmail.get(i) + ","
                     + newUserGender.get(i) + ","  + newUserHeight.get(i) + "," + newUserWeight.get(i)  + "," + newGroup.get(i)); //
        }

        try {
            //saving the file into device
            FileOutputStream out = getActivity().openFileOutput("UserProfile.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes()); // writing data here
            out.close();

            //exporting
            Context context = getContext();
            File filelocation = new File(getActivity().getFilesDir(), "UserProfile.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.fyp_analysis", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "User Profiles");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Export to Google Drive"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportActivityData() {
        //generate data
        for (int i = 0; i < newUserID.size(); i++) {
            userID += newUserID.get(i) + ",";
        }
        StringBuilder data = new StringBuilder();
        data.append("Steps\nDate," + userStepsID  ); //+ nameArray
        for (int i = 0; i < newDate.size(); i++) {
            data.append("\n" + newDate.get(i) + "," + mainArray.get(i)); //
        }

        try {
            //saving the file into device
            FileOutputStream out = getActivity().openFileOutput("UserActivities.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes()); // writing data here
            out.close();

            //exporting
            Context context = getContext();
            File filelocation = new File(getActivity().getFilesDir(), "UserActivities.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.fyp_analysis", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "User Activities");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Export to Google Drive"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSteps(){
        newDate=new ArrayList<>();
        userSteps=new ArrayList<>();
        userStepsID=new ArrayList<>();
        mainArray = new ArrayList<ArrayList<String>>();

            final DatabaseReference databaseReference = firebaseDatabase.getReference("Steps Count/" );
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()) {
                        for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) { //all the dates
                            if(myDataSnapshot.hasChildren()){
                                for (DataSnapshot meDataSnapshot : myDataSnapshot.getChildren()) {
                                    newDate.add(String.valueOf(meDataSnapshot.getKey()));
                                }
                            }
                        }
                        //check newDate Array for same date
                        Set<String> set = new HashSet<>(newDate);
                        newDate.clear();
                        newDate.addAll(set);
                        Collections.sort(newDate);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });

            //check for all steps for each user
        final DatabaseReference StepsdatabaseReference = firebaseDatabase.getReference("Steps Count/" );
        StepsdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //retrieve all the users
                mainArray = new ArrayList<ArrayList<String>>(); //create 2D array list
                if(dataSnapshot.hasChildren()) {
                    if (newDate != null) {
                        for (int i = 0; i < newDate.size(); i++) { //only until end of each user
                            userSteps = new ArrayList<>(); //new set of user
                            for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) { //all the users
                                userStepsID=new ArrayList<>();
                                userStepsID.add(myDataSnapshot.getKey());
                                if (myDataSnapshot.hasChildren()) {
                                    if (myDataSnapshot.child(newDate.get(i)).child("steps").getValue() == null) {
                                        userSteps.add("null");
                                    } else {
                                        userSteps.add(String.valueOf(myDataSnapshot.child(newDate.get(i)).child("steps").getValue())); //get steps
                                    }
                                }
                            }
                            // Log.d("myTag", dateSnapshot.getKey());
                            //Log.d("myTag2", String.valueOf(myDataSnapshot.child(newDate.get(i)).child("steps").getValue()));
                            mainArray.add(userSteps); //add each date of steps
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public static boolean bruteforce(String[] input) {
//        for (int i = 0; i < input.length; i++) {
//            for (int j = 0; j < input.length; j++) {
//                if (input[i].equals(input[j]) && i != j) {
//                    input[i] = ArrayUtils.remove(input[i], i);
//                }
//            }
//        }
//        return false;
//    }
}
