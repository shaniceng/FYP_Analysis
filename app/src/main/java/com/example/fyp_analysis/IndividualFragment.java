package com.example.fyp_analysis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
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
    private String user, userID = "";

    private RecyclerView mrecyclerView;
    private MyAdapter mAdapter;
//    private RecyclerView.LayoutManager mlayoutManager;

    private DatabaseReference mDatabase,AGEdatabaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<String> newUserProfiles;
    private ArrayList<String> newUserID;
    private ArrayList<String> newUserAge;
    private ArrayList<String> newUserEmail;
    private ArrayList<String> newUserHeight;
    private ArrayList<String> newUserWeight;
    private ArrayList<String> newGroup;
    private ArrayList<String> newUserGender;
    private ArrayList<String> newDate;
    private ArrayList<String> userSteps;
    private ArrayList<String> userStepsID;
    private ArrayList<ArrayList<String>> mainArray;
    private ArrayList<String> newActivityDate;
    private ArrayList<String> userActivitiesID;
    private ArrayList<String> userActivities;
    private ArrayList<ArrayList<String>> mainActivityArray;
    private ArrayList<ArrayList<String>> newActivityDatePerP;
    private ArrayList<Integer> userActivitiesMAX;
    private ArrayList<String> newMODActivityDate;
    private ArrayList<String> userMODActivitiesID;
    private ArrayList<String> userMODActivities;
    private ArrayList<ArrayList<String>> mainMODActivityArray;
    private ArrayList<ArrayList<String>> newMODActivityDatePerP;
    private int Age;
    private int sizeOfPreviousUser = 0;
    private SharedPreferences prefs;

    private EditText searchEdit;
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
//        exportData = v.findViewById(R.id.Export_everything);
        exportActivities = v.findViewById(R.id.btnExportActivities);
        searchEdit = v.findViewById(R.id.searchBar);
        firebaseDatabase = FirebaseDatabase.getInstance();
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        getUserName();
        getSteps();
        getUserActivities();
//        getModerateMins();
        InsertRecyclerView();
//        exportData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //getUserProfile();
//                export();
//            }
//        });
        exportActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportActivityData();
            }
        });

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    public void getUserName() {
        //firebaseDatabase=FirebaseDatabase.getInstance();
        newUserProfiles = new ArrayList<>();
        newUserID = new ArrayList<>();
        newUserAge = new ArrayList<>();
        newUserEmail = new ArrayList<>();
        newUserHeight = new ArrayList<>();
        newUserWeight = new ArrayList<>();
        newGroup = new ArrayList<>();
        newUserGender = new ArrayList<>();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Users/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                        UserProfile userProfile = myDataSnapshot.getValue(UserProfile.class);
                        newUserProfiles.add(userProfile.getUserName());
                        newUserID.add(myDataSnapshot.getKey());
                        newUserAge.add(userProfile.getUserAge());
                        newUserEmail.add(userProfile.getUserEmail());
                        newUserHeight.add(userProfile.getUserHeight());
                        newUserWeight.add(userProfile.getUserWeight());
                        newUserGender.add(userProfile.getUserGender());
                        if (userProfile.getGroup() != null)
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
        //mlayoutManager = new LinearLayoutManager(getContext());
        mrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mrecyclerView.setHasFixedSize(true);
        mAdapter = new MyAdapter(newUserID, newUserProfiles, this);
        mrecyclerView.setAdapter(mAdapter);

    }

    private void filter(String text) {
        ArrayList<String> filteredList = new ArrayList<>();

        for (String s : newUserProfiles) {
            //if the existing elements contains the search input
            if (s.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filteredList.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        mAdapter.filterList(filteredList);

    }


    @Override
    public void OnItemClick(int position) {
        //navigate to new activity
        //newUserID.get(position);
        Intent intent = new Intent(getActivity(), IndividualUserDetails.class);
        intent.putExtra("UserID", newUserID.get(position));
        startActivity(intent);
        Log.d("RECYLCERVIEW", "CLICKED" + position);

    }


    //    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater = getActivity().getMenuInflater();
//        inflater.inflate(R.menu.userprofile_menu,menu);
//
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //MyAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//    }
//
//    private void export() {
//        //generate data
//        StringBuilder data = new StringBuilder();
//        data.append("Users,Name,Age,Email,Gender,Height,Weight,Group"); //,Group
//        for (int i = 0; i < newUserID.size(); i++) {
//            data.append("\n" + newUserID.get(i) + "," + newUserProfiles.get(i) + "," + newUserAge.get(i) + "," + newUserEmail.get(i) + ","
//                     + newUserGender.get(i) + ","  + newUserHeight.get(i) + "," + newUserWeight.get(i)  + "," + newGroup.get(i)); //
//        }
//
//        try {
//            //saving the file into device
//            FileOutputStream out = getActivity().openFileOutput("UserProfile.csv", Context.MODE_PRIVATE);
//            out.write((data.toString()).getBytes()); // writing data here
//            out.close();
//
//            //exporting
//            Context context = getContext();
//            File filelocation = new File(getActivity().getFilesDir(), "UserProfile.csv");
//            Uri path = FileProvider.getUriForFile(context, "com.example.fyp_analysis", filelocation);
//            Intent fileIntent = new Intent(Intent.ACTION_SEND);
//            fileIntent.setType("text/csv");
//            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "User Profiles");
//            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
//            startActivity(Intent.createChooser(fileIntent, "Export to Google Drive"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void exportActivityData() {
        //generate data
        for (int i = 0; i < newUserID.size(); i++) {
            userID += newUserID.get(i) + ",";
        }
        StringBuilder data = new StringBuilder();
        data.append("Users,Name,Age,Email,Gender,Height,Weight,Group"); //,Group
        for (int i = 0; i < newUserID.size(); i++) {
            data.append("\n" + newUserID.get(i) + "," + newUserProfiles.get(i) + "," + newUserAge.get(i) + "," + newUserEmail.get(i) + ","
                    + newUserGender.get(i) + "," + newUserHeight.get(i) + "," + newUserWeight.get(i) + "," + newGroup.get(i)); //
        }
        data.append("\n\n\nSteps Count\nDate, UserID\n ," + userStepsID); //+ nameArray
        for (int i = 0; i < newDate.size(); i++) {
            data.append("\n" + newDate.get(i) + "," + mainArray.get(i)); //
        }
        data.append("\n\n\nActivity Tracker\nUserID, Date,Activities"); //+ userActivitiesID
        for (int i = 0; i < userActivitiesID.size(); i++) { //assign the values to get first user
            if (sizeOfPreviousUser != newActivityDatePerP.get(i).size()) {
                for (int n = 0; n < newActivityDatePerP.get(i).size(); n++) {
                    data.append("\n" + userActivitiesID.get(i) + "," + newActivityDatePerP.get(i).get(n) + "," + mainActivityArray.get(n + sizeOfPreviousUser)); //\n name,Date,Activities + "," + mainActivityArray.get(n)
                    //Log.d("myTag10000", String.valueOf(mainActivityArray.get(n)));
                }
                sizeOfPreviousUser += newActivityDatePerP.get(i).size();
            } else {
                Toast.makeText(getActivity(), "Please Refresh App and Try Again", Toast.LENGTH_SHORT).show();
            }

        }
        sizeOfPreviousUser = 0;
        data.append("\n\n\nModerate Mins\nUserID, Date, Activities");
        for (int i = 0; i < userActivitiesID.size(); i++) { //assign the values to get first user
            if (sizeOfPreviousUser != newActivityDatePerP.get(i).size()) {
                for (int n = 0; n < newActivityDatePerP.get(i).size(); n++) {
                    data.append("\n" + userActivitiesID.get(i) + "," + newActivityDatePerP.get(i).get(n) + "," + mainMODActivityArray.get(n + sizeOfPreviousUser)); //\n name,Date,Activities + "," + mainActivityArray.get(n)
                    //Log.d("myTag10000", String.valueOf(mainActivityArray.get(n)));
                }
                sizeOfPreviousUser += newActivityDatePerP.get(i).size();
            } else {
                Toast.makeText(getActivity(), "Please Refresh App and Try Again", Toast.LENGTH_SHORT).show();
            }

        }
        sizeOfPreviousUser = 0;

        try {
            //saving the file into device
            FileOutputStream out = getActivity().openFileOutput("User Profiles & Activities.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes()); // writing data here
            out.close();

            //exporting
            Context context = getContext();
            File filelocation = new File(getActivity().getFilesDir(), "User Profiles & Activities.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.fyp_analysis", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "User Profiles & Activities");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Export to Google Drive"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSteps() {
        newDate = new ArrayList<>();
        userSteps = new ArrayList<>();
        userStepsID = new ArrayList<>();
        mainArray = new ArrayList<ArrayList<String>>();

        final DatabaseReference databaseReference = firebaseDatabase.getReference("Steps Count/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) { //all the dates
                        if (myDataSnapshot.hasChildren()) {
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
        final DatabaseReference StepsdatabaseReference = firebaseDatabase.getReference("Steps Count/");
        StepsdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //retrieve all the users
                mainArray = new ArrayList<ArrayList<String>>(); //create 2D array list
                if (dataSnapshot.hasChildren()) {
                    if (newDate != null) {
                        for (int i = 0; i < newDate.size(); i++) { //only until end of each user
                            userSteps = new ArrayList<>(); //new set of user
                            userStepsID = new ArrayList<>();
                            for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) { //all the users
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

    private void getUserActivities() {
        newActivityDate = new ArrayList<>();
        userActivities = new ArrayList<>();
        userActivitiesID = new ArrayList<>();
        mainActivityArray = new ArrayList<ArrayList<String>>();
        newActivityDatePerP = new ArrayList<ArrayList<String>>();

        newMODActivityDate = new ArrayList<>();
        userMODActivities = new ArrayList<>();
        userMODActivitiesID = new ArrayList<>();
        mainMODActivityArray = new ArrayList<ArrayList<String>>();
        newMODActivityDatePerP = new ArrayList<ArrayList<String>>();

        mDatabase = FirebaseDatabase.getInstance().getReference("Activity Tracker/");

        final DatabaseReference databaseReference = firebaseDatabase.getReference("Activity Tracker/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userActivitiesID = new ArrayList<>();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) { //all the dates
                        userActivitiesID.add(myDataSnapshot.getKey());
                        if (myDataSnapshot.hasChildren()) {
                            newActivityDate = new ArrayList<>();
                            for (DataSnapshot meDataSnapshot : myDataSnapshot.getChildren()) {
                                newActivityDate.add(String.valueOf(meDataSnapshot.getKey()));
                            }
                            newActivityDatePerP.add(newActivityDate);
                        }
                    }
                }

                mainActivityArray = new ArrayList<ArrayList<String>>(); //create 2D array list
                if (userActivitiesID.size() != 0 && newActivityDatePerP.size() != 0) {
                    for (int i = 0; i < userActivitiesID.size(); i++) {
                        for (int n = 0; n < newActivityDatePerP.get(i).size(); n++) {
                            //create array for each date
                            //Log.d("myTag2", String.valueOf(newActivityDatePerP.get(i).get(n)));
                            //Log.d("myTag3", String.valueOf(userActivitiesID.get(i)));
                            AGEdatabaseReference = firebaseDatabase.getReference("Users/" + userActivitiesID.get(i));
                            mDatabase.child(userActivitiesID.get(i)).child(newActivityDatePerP.get(i).get(n)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        userActivities = new ArrayList<>(); //create array for each date
                                        userMODActivities = new ArrayList<>();
                                        for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                                            if (myDataSnapshot.hasChildren()) {
                                                LockInValue lockInValue = myDataSnapshot.getValue(LockInValue.class);
                                                    AGEdatabaseReference.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                                                            if (userProfile.getUserAge() != null) {
                                                                Age = 220 - Integer.parseInt(userProfile.getUserAge());
                                                                //userActivitiesMAX.add(Age); //know my max hr alr
                                                                Log.d("AGE HERE NOW HELLO", String.valueOf(Age));
                                                                if (!prefs.contains("AGE")) {
                                                                    SharedPreferences.Editor editor = prefs.edit();
                                                                    editor.putInt("AGE", Age);
                                                                    editor.commit();
                                                                } else {
                                                                    SharedPreferences.Editor editor = prefs.edit();
                                                                    editor.putInt("AGE", Age);
                                                                    editor.commit();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                    userActivities.add(String.valueOf(myDataSnapshot.getValue()));
                                                    Log.d("myTag", String.valueOf(myDataSnapshot.getValue()));
                                                    if (lockInValue.getAvrHeartRate() != null) {
                                                        Log.d("HEREEEEEEEEEEEEEEEEEE", String.valueOf(Float.parseFloat(lockInValue.getAvrHeartRate().replaceAll("[^\\d.]", ""))));
                                                        Log.d("LOLOLOLOLOLOLOLOL", String.valueOf(0.5 * Age));
                                                        if (Float.parseFloat(lockInValue.getAvrHeartRate().replaceAll("[^\\d.]", "")) >= (0.5 * prefs.getInt("AGE", 0))
                                                                && (Float.parseFloat(lockInValue.getAvrHeartRate().replaceAll("[^\\d.]", "")) <= prefs.getInt("AGE", 0))) { //compare with hr, if moderate
                                                            userMODActivities.add(String.valueOf(myDataSnapshot.getValue()));
                                                            Log.d("HEREEEEEEEEEEEEEEEEEE", String.valueOf(myDataSnapshot.getValue()));
                                                        } else {
                                                            Log.d("HEREEEEEEEEEEEEEEEEEE", "OUTTTTTTT");
                                                            userMODActivities.add("null");
                                                        }
                                                    } else {
                                                        userMODActivities.add("null");
                                                    }
                                            } else {
                                                userActivities.add("null");
                                                userMODActivities.add("null");
                                            }
                                        }
                                        mainActivityArray.add(userActivities);
                                        mainMODActivityArray.add(userMODActivities);
                                    } else {
                                        //reset array user actiities
                                        userActivities = new ArrayList<>();
                                        userActivities.add("null");
                                        mainActivityArray.add(userActivities);

                                        userMODActivities = new ArrayList<>();
                                        userMODActivities.add("null");
                                        mainMODActivityArray.add(userMODActivities);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } else {
                    //Log.d("myTag4", "failed failed failed ------------------------------");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
