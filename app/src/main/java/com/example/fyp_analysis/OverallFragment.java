package com.example.fyp_analysis;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverallFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PieChart control_steps_chart, intervention_steps_chart;
    //private AnyChartView intervention_steps_chart; //control_steps_chart
    String[] months ={"Jan", "Feb", "Mar"};
    int[] earnings ={500, 800,2000};
    int[] colour = new int[]{ Color.BLUE, Color.LTGRAY,Color.CYAN, Color.DKGRAY, Color.RED};

    private FirebaseDatabase firebaseDatabase;
    private ArrayList<String>userID;
    private ArrayList<String>controlUserID;
    private ArrayList<String>interventionUserID;

    public OverallFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverallFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverallFragment newInstance(String param1, String param2) {
        OverallFragment fragment = new OverallFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overall, container, false);
        control_steps_chart = view.findViewById(R.id.Csteps_chart_view);
        intervention_steps_chart = view.findViewById(R.id.Isteps_chart_view);
        firebaseDatabase= FirebaseDatabase.getInstance();
        getStepsCount();
        setupPieControlStepsChart();
        setUpPieInterventionStepsChart();
        
        return view;
    }

    private ArrayList<PieEntry> datavals1(){
        ArrayList<PieEntry> datavals = new ArrayList<>();
        datavals.add(new PieEntry(15, ">7500"));
        datavals.add(new PieEntry(200, "<7500"));
        return datavals;

    }
    private void setupPieControlStepsChart() {
        PieDataSet pieDataSet = new PieDataSet(datavals1(),"");
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        control_steps_chart.setData(pieData);
        control_steps_chart.setDrawEntryLabels(true);
        control_steps_chart.setUsePercentValues(true);
        control_steps_chart.setCenterText("Percentage of >7500 steps per day");
        control_steps_chart.invalidate();

    }

    private ArrayList<PieEntry> datavals2(){
        ArrayList<PieEntry> datavals = new ArrayList<>();
        datavals.add(new PieEntry(700, ">7500"));
        datavals.add(new PieEntry(200, "<7500"));
        return datavals;

    }
    private void setUpPieInterventionStepsChart() {
        PieDataSet pieDataSet = new PieDataSet(datavals2(),"");
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        intervention_steps_chart.setData(pieData);
        intervention_steps_chart.setDrawEntryLabels(true);
        intervention_steps_chart.setUsePercentValues(true);
        intervention_steps_chart.setCenterText("Percentage of >7500 steps per day");
        intervention_steps_chart.invalidate();
    }

    private void getStepsCount(){ //to get the data from firebase
        // collect all users's steps count
        //check user id if is control or intervention group
        //if control, check steps count data
        userID = new ArrayList<>();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Steps Count/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userID = new ArrayList<>();
                controlUserID = new ArrayList<>();
                interventionUserID = new ArrayList<>();
                if(dataSnapshot.hasChildren()){
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                        userID.add(myDataSnapshot.getKey());
                        Log.d("mytag",myDataSnapshot.getKey() );
                    }

                    //get every user id in list alr, need to compare with user part
                    for(int i=0; i<userID.size(); i++) { //get every element
                        final DatabaseReference userDatabaseRef = firebaseDatabase.getReference("Users/").child(userID.get(i)); //get individual user
                        userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //read group
                                if(dataSnapshot.hasChildren()) {
                                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                                    if (userProfile.getGroup()== "control") {
                                        //add to control array
                                        controlUserID.add(userProfile.getGroup());
                                        Log.d("u", userProfile.getGroup());
                                    } else if (userProfile.getGroup() == "intervention") {
                                        //add to intervention array
                                        interventionUserID.add(userProfile.getGroup());
                                        Log.d("userConfirm", userProfile.getGroup());
                                    } else {
                                        Log.d("userConfirmIDINARRAYYYY", userProfile.getGroup()); //ERROR HERE CONTINUE HERE!!!___________________________
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }//end for loop


                    //now alr have each control grp lst and intervention list
                    //get steps count data with that list
                    if(interventionUserID.size()!=0) {
                        for (int i = 0; i < interventionUserID.size(); i++) {
                            Log.d("userID", "lololol--------------------------------------------");
                            final DatabaseReference userDatabaseRef = firebaseDatabase.getReference("Steps Count/").child(interventionUserID.get(i)); //get steps count
                            userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                                            Log.d("userID", String.valueOf(myDataSnapshot.getValue()));
                                        }
                                    } else {

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




//    to change colours in the future
//    dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
//    OR
//dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
//    OR
//dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//    OR
//dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
//    OR
//dataSet.setColors(ColorTemplate.PASTEL_COLORS);
}
