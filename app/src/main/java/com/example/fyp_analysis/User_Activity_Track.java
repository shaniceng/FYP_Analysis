package com.example.fyp_analysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class User_Activity_Track extends AppCompatActivity {

    private String userDate, userID;
    private TextView UserDate, UserSteps;
    private Button onBack;
    private FirebaseDatabase firebaseDatabase;
    private GraphView graphView;
    private LineGraphSeries lineGraphSeries;
    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
    private DatabaseReference heartDatabaseRef, lockinDataBaseRef;
    private ArrayList<String> mDataSet;
    private ArrayList<String> mTimeSet;
    private ArrayList<Float> mModerateMinsArray;
    private ArrayList<String> currentTimeA;
    private ArrayList <String> activityHeartRate;

    private RecyclerView mrecyclerView;
    private RecyclerView.LayoutManager mlayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user___track);
        firebaseDatabase= FirebaseDatabase.getInstance();
        mrecyclerView = findViewById(R.id.activity_RV);
        InsertRecyclerView();
        userDate = getIntent().getExtras().getString("UserDate");
        userID=getIntent().getExtras().getString("USERIDTRACK");
        UserDate=findViewById(R.id.tv_user_date);
        UserSteps=findViewById(R.id.tv_user_steps);
        onBack=findViewById(R.id.onBack);
        onBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        graphView= findViewById(R.id.graph);
        lineGraphSeries=new LineGraphSeries();
        UserDate.setText(userDate);
        getUserSteps();
        retrieveHeartData();
        showGraph();

        RetrieveLockInData();
    }

    public void getUserSteps(){
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Steps Count/" + userID +"/" + userDate);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    StepsPointValue stepsPointValue = dataSnapshot.getValue(StepsPointValue.class);
                    UserSteps.setText("Steps Count: " + stepsPointValue.getSteps());
//                    userAge.setText("Age: "+userProfile.getUserAge());
//                    userEmail.setText("Email: "+userProfile.getUserEmail());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(User_Activity_Track.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showGraph() {
        graphView.removeAllSeries();
        graphView.addSeries(lineGraphSeries);
        //graphView.removeSeries(lineGraphWeekly);
        graphView.setTitle("Heart Rate(BPM) Today:");
        graphView.getViewport().setMinX(new Date().getTime()-10800000);
        graphView.getViewport().setMaxX(new Date().getTime());
        graphView.getViewport().setMinY(50);
        graphView.getViewport().setMaxY(170);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);

        lineGraphSeries.setDrawBackground(true);

        lineGraphSeries.setBackgroundColor(Color.argb(100, 163, 180, 195));
        lineGraphSeries.setColor(Color.argb(255, 0, 51, 102));
        lineGraphSeries.setDrawDataPoints(true);
        lineGraphSeries.setDataPointsRadius(15);
        lineGraphSeries.setThickness(10);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX) {
                    return simpleDateFormat.format(new Date((long) value));
                }else {
                    return super.formatLabel(value, isValueX);
                }

            }
        });
        lineGraphSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(User_Activity_Track.this, "Heart Rate: "+dataPoint.getY() + "BPM", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveHeartData() {
        heartDatabaseRef = firebaseDatabase.getReference("Chart Values/" + userID +"/");
        heartDatabaseRef.child(userDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataPoint[] dataVals = new DataPoint[(int) dataSnapshot.getChildrenCount()];
                int index =0;

                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                        PointValue pointValue = myDataSnapshot.getValue(PointValue.class);
                        dataVals[index] = new DataPoint(pointValue.getxValue(),pointValue.getyValue());
                        index++;

//                        avrHeartRate.add(pointValue.getyValue());
//                        double heartRate=calculateAverageStepsOfCompetitors(avrHeartRate);
//                        ratedMaxHR.setText(String.format("%.1f", heartRate) + "BPM");
                    }
                    lineGraphSeries.resetData(dataVals);

                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void RetrieveLockInData() {
        lockinDataBaseRef = firebaseDatabase.getReference("Activity Tracker/" +userID + "/" + userDate );
        lockinDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.getChildrenCount();
                mDataSet = new ArrayList<>();
                mTimeSet=new ArrayList<>();
                //image = new ArrayList<>();
                currentTimeA = new ArrayList<>();
                mModerateMinsArray=new ArrayList<>();
                activityHeartRate = new ArrayList();
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                        LockInValue lockInValue = myDataSnapshot.getValue(LockInValue.class);
                        mTimeSet.add(lockInValue.getDuration());
                        mDataSet.add(lockInValue.getActivity());
                        currentTimeA.add(lockInValue.getcTime());
                        if((lockInValue.getAvrHeartRate()==null) ){
                            activityHeartRate.add("No heart rate detected");
                        }
                        else {
                            activityHeartRate.add("Average heart rate: " +lockInValue.getAvrHeartRate());
//                            activity_heart_ratey=lockInValue.getActivity();
//                            activity_heartRate=Float.parseFloat(lockInValue.getAvrHeartRate().replaceAll("[^0-9.]", ""));
                        }
                        //mAdapter.notifyDataSetChanged();
                        InsertRecyclerView();

//                        if(lockInValue.getDuration()!=null) {
//                            duration = Integer.parseInt(lockInValue.getDuration().replaceAll("[\\D]", ""));
//                            mins = duration / 100;
//                            sec = duration % 100;
//                            mModerateMinsArray.add(mins+(sec/60));
//                        }
                    }
//                    if (!prefs.contains("CHECK_IF_DISPLAYED_DIALOG")) {
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putLong("CHECK_IF_DISPLAYED_DIALOG", count-1);
//                        editor.commit();
//                    }
//                    if(prefs.getLong("CHECK_IF_DISPLAYED_DIALOG",count-1)!=count) {
//                        ShowAlertDialogWhenCompleteActivity();
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putLong("CHECK_IF_DISPLAYED_DIALOG", count);
//                        editor.commit();
//                    }
//                    insertWeeklyModerateMins();
//                    double mmarray=calculateSumOfModerateMins(mModerateMinsArray);
//                    moderateMins.setText("Moderate exercise today: " + String.format("%.1f",mmarray ) + "mins");


                }else{
                    Toast.makeText(User_Activity_Track.this,"No activity to retrieve", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(User_Activity_Track.this,"Error in retrieving activity", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //insert activity into home page
    public void InsertRecyclerView() {
        mlayoutManager = new LinearLayoutManager(this);
        mrecyclerView.setHasFixedSize(true);
        mAdapter = new CustomAdapter(mDataSet, mTimeSet, currentTimeA, activityHeartRate);
        mrecyclerView.setLayoutManager(mlayoutManager);
        mrecyclerView.setAdapter(mAdapter);

    }
}
