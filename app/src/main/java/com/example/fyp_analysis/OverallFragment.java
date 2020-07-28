package com.example.fyp_analysis;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
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
import java.util.Calendar;
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

    private PieChart control_steps_chart, intervention_steps_chart, control_mins_chart, intervention_mins_chart;
    //private AnyChartView intervention_steps_chart; //control_steps_chart
//    String[] months ={"Jan", "Feb", "Mar"};
//    int[] earnings ={500, 800,2000};
//    int[] colour = new int[]{ Color.BLUE, Color.LTGRAY,Color.CYAN, Color.DKGRAY, Color.RED};

    private FirebaseDatabase firebaseDatabase;
    private ArrayList<String> userID;
    private ArrayList<String> controlUserID;
    private ArrayList<String>interventionUserID;
    private ArrayList<Integer> controlMORESteps;
    private ArrayList<Integer> controlLESSSteps;
    private ArrayList<Integer> interventionMORESteps;
    private ArrayList<Integer> interventionLESSSteps;

    private ArrayList<Integer> controlMOREMins;
    private ArrayList<Integer> controlLESSMins;
    private ArrayList<Integer> interventionMOREMins;
    private ArrayList<Integer> interventionLESSMins;
    private int duration, mins, sec;
    private ArrayList<Float> mModerateMinsInterventionArray;
    private SharedPreferences prefs;

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
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        control_steps_chart = view.findViewById(R.id.Csteps_chart_view);
        intervention_steps_chart = view.findViewById(R.id.Isteps_chart_view);
        control_mins_chart=view.findViewById(R.id.Cmins_chart_view);
        intervention_mins_chart=view.findViewById(R.id.Imins_chart_view);
        firebaseDatabase= FirebaseDatabase.getInstance();
        getStepsCount();
//        getMins();

        
        return view;
    }

    private ArrayList<PieEntry> controlGrpStepsData(){
        ArrayList<PieEntry> datavals = new ArrayList<>();

        if(controlMORESteps!=null) {
            datavals.add(new PieEntry(controlMORESteps.size(), ">7500")); //Float.parseFloat(controlSteps.get(0))
            datavals.add(new PieEntry(controlLESSSteps.size(), "<7500"));//Float.parseFloat(controlSteps.get(1))
        }
        return datavals;
    }
    private void setupPieControlStepsChart() {
        PieDataSet pieDataSet = new PieDataSet(controlGrpStepsData(),"");
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        control_steps_chart.setData(pieData);
        control_steps_chart.setDrawEntryLabels(true);
        control_steps_chart.setUsePercentValues(true);
        control_steps_chart.setCenterText("Percentage of >7500 steps per day");
        control_steps_chart.invalidate();

    }

    private ArrayList<PieEntry> interventionGroupStepsData(){
        ArrayList<PieEntry> datavals = new ArrayList<>();
        if(interventionMORESteps!=null) {
            datavals.add(new PieEntry(interventionMORESteps.size(), ">7500")); //Float.parseFloat(interventionSteps.get(0))
            datavals.add(new PieEntry(interventionLESSSteps.size(), "<7500")); //Float.parseFloat(interventionSteps.get(1))
            Log.d("HELPPPPPP", String.valueOf(interventionMORESteps.size()));
            Log.d("LOLOLOLO", String.valueOf(interventionLESSSteps.size()));
        }

        return datavals;

    }
    private void setUpPieInterventionStepsChart() {
        PieDataSet pieDataSet = new PieDataSet(interventionGroupStepsData(),"");
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
                 controlMORESteps=new ArrayList<>();
                 controlLESSSteps=new ArrayList<>();
                 interventionMORESteps=new ArrayList<>();
                 interventionLESSSteps=new ArrayList<>();

                if(dataSnapshot.hasChildren()){
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                        userID.add(myDataSnapshot.getKey()); //get every user id
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
                                    String userId = dataSnapshot.getKey();
                                    if(userProfile.getGroup()!=null) {
                                        switch (userProfile.getGroup()) {
                                            case "control":
                                                controlUserID.add(userId); //get individual person
                                                Log.d("u", userId);
                                                break;
                                            case "intervention":
                                                interventionUserID.add(userId);
                                                Log.d("userConfirm", userId);
                                                break;
                                            default:
                                                Log.d("userConfirmIDINARRAYYYY", userProfile.getGroup());
                                                break;
                                        }
                                    }
                                    //get steps
//                                    controlSteps=new ArrayList<>();
//                                    interventionSteps=new ArrayList<>();
                                    controlMORESteps = new ArrayList<>();
                                    controlLESSSteps = new ArrayList<>();
                                    interventionMORESteps = new ArrayList<>();
                                    interventionLESSSteps = new ArrayList<>();
                                    if (interventionUserID.size() != 0) {
                                        for (int i = 0; i < interventionUserID.size(); i++) { //get every element
                                            final DatabaseReference userStepsDatabaseRef = firebaseDatabase.getReference("Steps Count/").child(interventionUserID.get(i)); //get individual user
                                            userStepsDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    //get individual steps
                                                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                                                        StepsPointValue stepsPointValue = myDataSnapshot.getValue(StepsPointValue.class);
                                                        if (stepsPointValue.getSteps() >= 7500) {
                                                            interventionMORESteps.add((int) stepsPointValue.getSteps());
                                                        } else if (stepsPointValue.getSteps() < 7500) {
                                                            interventionLESSSteps.add((int) stepsPointValue.getSteps());
                                                            Log.d("userConfirmSTEPSSSS", String.valueOf(stepsPointValue.getSteps()));
                                                        }
                                                        setUpPieInterventionStepsChart();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    if (controlUserID.size() != 0) {
                                        for (int i = 0; i < controlUserID.size(); i++) { //get every element
                                            final DatabaseReference userStepsDatabaseRef = firebaseDatabase.getReference("Steps Count/").child(controlUserID.get(i)); //get individual user
                                            userStepsDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    //get individual steps
                                                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                                                        StepsPointValue stepsPointValue = myDataSnapshot.getValue(StepsPointValue.class);
                                                        if (stepsPointValue.getSteps() >= 7500) {
                                                            controlMORESteps.add((int) stepsPointValue.getSteps());
                                                        } else if (stepsPointValue.getSteps() < 7500) {
                                                            controlLESSSteps.add((int) stepsPointValue.getSteps());
                                                            Log.d("userConfirmSTEPSSSS", String.valueOf(stepsPointValue.getSteps()));
                                                        }
                                                        setupPieControlStepsChart();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    getMins();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }//end for loop

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //start of mins code
    private ArrayList<PieEntry> controlMinsData(){
        ArrayList<PieEntry> datavals = new ArrayList<>();

        if(controlMOREMins!=null) {
            datavals.add(new PieEntry(controlMOREMins.size(), ">150 mins"));
            datavals.add(new PieEntry(controlLESSMins.size(), "<150 mins"));
        }
        return datavals;
    }
    private void setupPieControlMinsChart() {
        PieDataSet pieDataSet = new PieDataSet(controlMinsData(),"");
        pieDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        PieData pieData = new PieData(pieDataSet);
        control_mins_chart.setData(pieData);
        control_mins_chart.setDrawEntryLabels(true);
        control_mins_chart.setUsePercentValues(true);
        control_mins_chart.setCenterText("Percentage of >150 mins per week");
        control_mins_chart.invalidate();

    }

    private ArrayList<PieEntry> interventionMinsData(){
        ArrayList<PieEntry> datavals = new ArrayList<>();
        if(interventionMOREMins!=null) {
            datavals.add(new PieEntry(interventionMOREMins.size(), ">7500"));
            datavals.add(new PieEntry(interventionLESSMins.size(), "<7500"));
        }
        return datavals;
    }

    private void setUpPieInterventionMinsChart() {
        PieDataSet pieDataSet = new PieDataSet(interventionMinsData(),"");
        pieDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        PieData pieData = new PieData(pieDataSet);
        intervention_mins_chart.setData(pieData);
        intervention_mins_chart.setDrawEntryLabels(true);
        intervention_mins_chart.setUsePercentValues(true);
        intervention_mins_chart.setCenterText("Percentage of >150 mins per week");
        intervention_mins_chart.invalidate();
    }


    private void getMins(){
        if(userID!=null){
            controlMOREMins = new ArrayList<>();
            controlLESSMins = new ArrayList<>();
            interventionMOREMins = new ArrayList<>();
            interventionLESSMins = new ArrayList<>();
            mModerateMinsInterventionArray=new ArrayList<>();
            if (interventionUserID.size() != 0) {
                for (int i = 0; i < interventionUserID.size(); i++) { //get every element
                    final DatabaseReference userMinsDatabaseRef = firebaseDatabase.getReference("Activity Tracker/").child(interventionUserID.get(i)); //get individual user
                    userMinsDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //get individual mins
                            if(dataSnapshot.hasChildren()) {
                                for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                                    if(myDataSnapshot.hasChildren()) {
                                        for (DataSnapshot minsDataSnapshot : myDataSnapshot.getChildren()) {
                                            LockInValue lockInValue = minsDataSnapshot.getValue(LockInValue.class);
                                            //differentiate week number here
                                            Log.d("userConfirmDateS", myDataSnapshot.getKey());
                                            if (!prefs.contains("weekNumberChecking")) {
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putInt("weekNumberChecking", 0);
                                                editor.commit();
                                            }
                                            int year = Integer.parseInt(myDataSnapshot.getKey())/10000;
                                            int month = (Integer.parseInt(myDataSnapshot.getKey())%10000)/100;
                                            int date = Integer.parseInt(myDataSnapshot.getKey())%100;
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.set(Calendar.YEAR, year);
                                            calendar.set(Calendar.MONTH, month);
                                            calendar.set(Calendar.DATE, date);
                                            int weekNo = calendar.get(Calendar.WEEK_OF_YEAR);
                                            Log.d("userweek", String.valueOf(weekNo));

                                            if(weekNo == (prefs.getInt("weekNumberChecking", 0))){
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putInt("weekNumberChecking", weekNo);
                                                editor.commit();

                                                if (lockInValue.getDuration() != null) {
                                                    duration = Integer.parseInt(lockInValue.getDuration().replaceAll("[\\D]", ""));
                                                    mins = duration / 100;
                                                    sec = duration % 100;
                                                    mModerateMinsInterventionArray.add((float) (mins+(sec/60)));
                                                    double mIarray = calculateSumOfModerateMins(mModerateMinsInterventionArray);
                                                    if (mIarray >= 150) { //for everyweek
                                                        interventionMOREMins.add((int) mIarray);

                                                    } else if (mIarray < 150) {
                                                        interventionLESSMins.add((int) mIarray);
                                                        Log.d("userConfirmMINSSS", String.valueOf(mIarray));
                                                    }
                                                }


                                            }else{
                                                //send to weeklyModerateMins
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putInt("weekNumberChecking", weekNo);
                                                editor.commit();
                                                mModerateMinsInterventionArray=new ArrayList<>();

                                                if (lockInValue.getDuration() != null) {
                                                    duration = Integer.parseInt(lockInValue.getDuration().replaceAll("[\\D]", ""));
                                                    mins = duration / 100;
                                                    sec = duration % 100;
                                                    mModerateMinsInterventionArray.add((float) (mins+(sec/60)));
                                                    double mIarray = calculateSumOfModerateMins(mModerateMinsInterventionArray);
                                                    if (mIarray >= 150) { //for everyweek
                                                        interventionMOREMins.add((int) mIarray);

                                                    } else if (mIarray < 150) {
                                                        interventionLESSMins.add((int) mIarray);
                                                        Log.d("userConfirmMINSSS", String.valueOf(mIarray));
                                                    }
                                                }
                                            }
                                            setUpPieInterventionMinsChart();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            if (controlUserID.size() != 0) {
                for (int i = 0; i < controlUserID.size(); i++) { //get every element
                    final DatabaseReference userMinsDatabaseRef = firebaseDatabase.getReference("Activity Tracker/").child(controlUserID.get(i)); //get individual user
                    userMinsDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //get individual mins
                            if(dataSnapshot.hasChildren()) {
                                for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                                    if (myDataSnapshot.hasChildren()) {
                                        for (DataSnapshot minsDataSnapshot : myDataSnapshot.getChildren()) {
                                            LockInValue lockInValue = minsDataSnapshot.getValue(LockInValue.class);
                                            //differentiate week number here
                                            Log.d("userConfirmDateS", myDataSnapshot.getKey());
                                            if (!prefs.contains("weekNumberChecking")) {
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putInt("weekNumberChecking", 0);
                                                editor.commit();
                                            }
                                            int year = Integer.parseInt(myDataSnapshot.getKey())/10000;
                                            int month = (Integer.parseInt(myDataSnapshot.getKey())%10000)/100;
                                            int date = Integer.parseInt(myDataSnapshot.getKey())%100;
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.set(Calendar.YEAR, year);
                                            calendar.set(Calendar.MONTH, month);
                                            calendar.set(Calendar.DATE, date);
                                            int weekNo = calendar.get(Calendar.WEEK_OF_YEAR);
                                            Log.d("userweek", String.valueOf(weekNo));

                                            if(weekNo == (prefs.getInt("weekNumberChecking", 0))){
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putInt("weekNumberChecking", weekNo);
                                                editor.commit();

                                                if (lockInValue.getDuration() != null) {
                                                    duration = Integer.parseInt(lockInValue.getDuration().replaceAll("[\\D]", ""));
                                                    mins = duration / 100;
                                                    sec = duration % 100;
                                                    mModerateMinsInterventionArray.add((float) (mins+(sec/60)));
                                                    double mIarray = calculateSumOfModerateMins(mModerateMinsInterventionArray);
                                                    if (mIarray >= 150) { //for everyweek
                                                        interventionMOREMins.add((int) mIarray);

                                                    } else if (mIarray < 150) {
                                                        interventionLESSMins.add((int) mIarray);
                                                        Log.d("userConfirmMINSSS", String.valueOf(mIarray));
                                                    }
                                                }
                                            }else{
                                                //send to weeklyModerateMins
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putInt("weekNumberChecking", weekNo);
                                                editor.commit();
                                                mModerateMinsInterventionArray=new ArrayList<>();

                                                if (lockInValue.getDuration() != null) {
                                                    duration = Integer.parseInt(lockInValue.getDuration().replaceAll("[\\D]", ""));
                                                    mins = duration / 100;
                                                    sec = duration % 100;
                                                    mModerateMinsInterventionArray.add((float) (mins+(sec/60)));
                                                    double mIarray = calculateSumOfModerateMins(mModerateMinsInterventionArray);
                                                    if (mIarray >= 150) { //for everyweek
                                                        controlMOREMins.add((int) mIarray);

                                                    } else if (mIarray < 150) {
                                                        controlLESSMins.add((int) mIarray);
                                                        Log.d("userConfirmMINSSS", String.valueOf(mIarray));
                                                    }
                                                }
                                                setupPieControlMinsChart();
                                            }
                                        }
                                    }
                                }
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

    private double calculateSumOfModerateMins(ArrayList<Float> moderateMins){
        double sum = 0;
        for(int i = 0; i < moderateMins.size(); i++)
            sum += moderateMins.get(i);
        return sum;
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
