package com.example.fyp_analysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.view.View.GONE;

public class IndividualUserDetails extends AppCompatActivity implements MyUserDetailsAdapter.OnDateListener{

    private TextView userID, userName, userAge, userEmail, stepsMOREDays, stepsLESSDays ;
    private FirebaseDatabase firebaseDatabase;
    private String user, username;
    private Button onBack, exportActivities,Stepbtn,activitybtn;
    private ArrayList<String>userDate;
    private ArrayList<String>userActivitiesDate;
    private ArrayList<String>userSteps;
    private ArrayList<String>userActivityDuration;
    private ArrayList<String>userActivityTime;
    private ArrayList<String>userActivityName;
    private ArrayList<String>userActivityHR;
    private ArrayList<String>userHeartTime;
    private ArrayList<String>userHeart;
    private ArrayList<String>userHeartDate;
    private ArrayList<Integer>stepsMoreDay;
    private ArrayList<Integer>stepsLessDay;

    private RecyclerView mrecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mlayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_user_details);
        user = getIntent().getExtras().getString("UserID");
        firebaseDatabase= FirebaseDatabase.getInstance();
        userID=findViewById(R.id.UserIDDetails);
        userName=findViewById(R.id.UserNameDetails);
        userAge=findViewById(R.id.UserAgeDetails);
        userEmail=findViewById(R.id.UserEmailDetails);
        exportActivities=findViewById(R.id.btnExportActivities);
        mrecyclerView = findViewById(R.id.UserTrackerRecyclerVew);
        stepsMOREDays = findViewById(R.id.tvStepsDays);
        stepsLESSDays = findViewById(R.id.tvStepsLESSDays);
        InsertRecyclerView();
        onBack=findViewById(R.id.onBack);
        onBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        userID.setText("User ID: " + user);

        Stepbtn=findViewById(R.id.steps_btn);
        Stepbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IndividualUserDetails.this, User_Step_Only.class);
                intent.putExtra("UserID", user);
                startActivity(intent);
            }
        });
        activitybtn = findViewById(R.id.activity_btn);
        activitybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(IndividualUserDetails.this, History_Activity.class);
                intent.putExtra("UserID", user);
                startActivity(intent);

            }
        });
        getUserDetails();
        getUserDaily();
        getUserActivities();
        getUserHeartRate();
        exportActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getUserProfile();
                export();
            }
        });

        getStepsTotal();
    }


    public void getUserDetails(){
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Users/" + user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    userName.setText("Name: "+ userProfile.getUserName());
                    userAge.setText("Age: "+userProfile.getUserAge());
                    userEmail.setText("Email: "+userProfile.getUserEmail());
                    username=userProfile.getUserName();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(IndividualUserDetails.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getUserDaily(){
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Steps Count/" + user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userDate=new ArrayList<>();
                userSteps=new ArrayList<>();
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
//                        UserDetails userDetails = myDataSnapshot.getValue(UserDetails.class);
                        userDate.add(myDataSnapshot.getKey());
                        if(myDataSnapshot.hasChildren()){
                            for (DataSnapshot meDataSnapshot : myDataSnapshot.getChildren()) {
//                                StepsPointValue stepsPointValue = meDataSnapshot.getValue(StepsPointValue.class);
                                userSteps.add(String.valueOf(meDataSnapshot.getValue()));
                            }
                        }
                    }
                    //mAdapter.notifyDataSetChanged();
                    InsertRecyclerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(IndividualUserDetails.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getUserActivities(){
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Activity Tracker/" + user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userActivitiesDate=new ArrayList<>();
                userActivityDuration=new ArrayList<>();
                userActivityTime=new ArrayList<>();
                userActivityName=new ArrayList<>();
                userActivityHR=new ArrayList<>();
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                        if(myDataSnapshot.hasChildren()){
                            for (DataSnapshot meDataSnapshot : myDataSnapshot.getChildren()) {
                                LockInValue lockInValue = meDataSnapshot.getValue(LockInValue.class);
                                userActivitiesDate.add(myDataSnapshot.getKey());
                                userActivityDuration.add(lockInValue.getDuration());
                                userActivityName.add(lockInValue.getActivity());
                                userActivityTime.add(lockInValue.getcTime());
                                if(lockInValue.getAvrHeartRate()==null){
                                    userActivityHR.add("null");
                                }
                                else {
                                    userActivityHR.add(lockInValue.getAvrHeartRate());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(IndividualUserDetails.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserHeartRate(){
        final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Chart Values/" + user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userHeartDate=new ArrayList<>();
                userHeart=new ArrayList<>();
                userHeartTime=new ArrayList<>();
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
                        if(myDataSnapshot.hasChildren()){
                            for (DataSnapshot meDataSnapshot : myDataSnapshot.getChildren()) {
                                userHeartDate.add(myDataSnapshot.getKey());
                                PointValue pointValue = meDataSnapshot.getValue(PointValue.class);
                                userHeart.add(String.valueOf(pointValue.getyValue()));
                                String time =  simpleDateFormat.format(new Date((long) pointValue.getxValue()));
                                userHeartTime.add(time);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(IndividualUserDetails.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void InsertRecyclerView() {
        mlayoutManager = new LinearLayoutManager(IndividualUserDetails.this);
        mrecyclerView.setLayoutManager(mlayoutManager);
        mrecyclerView.setHasFixedSize(true);
        mAdapter = new MyUserDetailsAdapter(userDate,this);
        mrecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void OnDateClick(int position) {
        Intent intent=new Intent(IndividualUserDetails.this, User_Activity_Track.class);
        intent.putExtra("USERIDTRACK", user);
        intent.putExtra("UserDate", userDate.get(position));
        startActivity(intent);
//        Log.d("RECYLCERVIEW","CLICKED" + position);
    }

    private void export() {
        //generate data

        StringBuilder data = new StringBuilder();
        data.append("Date,Steps Count"); //,,Activities
        for (int i = 0; i < userDate.size(); i++) {
            data.append("\n" + userDate.get(i) + "," + userSteps.get(i));// + "," + userActivity.get(i)); //
        }
        data.append("\n\n\nActivities\nDate,Activity Name,Duration,Time"); //get activities in dates
        for (int i = 0; i < userActivitiesDate.size(); i++) {
            data.append("\n" + userActivitiesDate.get(i) + "," + userActivityName.get(i) + "," + userActivityDuration.get(i)
                    + "," + userActivityTime.get(i));// + "," + userActivity.get(i)); //
        }
        data.append("\n\n\nDate and Time,Heart Rate"); //get activities in dates
        for (int i = 0; i < userHeartDate.size(); i++) {
            data.append("\n" + userHeartTime.get(i) + "," + userHeart.get(i));// + "," + userActivity.get(i)); //
        }

        try {
            //saving the file into device
            FileOutputStream out = IndividualUserDetails.this.openFileOutput(username +"Activities.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes()); // writing data here
            out.close();

            //exporting
            Context context = IndividualUserDetails.this;
            File filelocation = new File(IndividualUserDetails.this.getFilesDir(), username +"Activities.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.fyp_analysis", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, username + " Activities");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Export to Google Drive"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getStepsTotal(){
        //get total number of days that has more than 7500 steps
        stepsMoreDay=new ArrayList<>();
        stepsLessDay=new ArrayList<>();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Steps Count/" + user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot : dataSnapshot.getChildren()){
                        StepsPointValue stepsPointValue = myDataSnapshot.getValue(StepsPointValue.class);
                        if(stepsPointValue.getSteps() >= 7500){
                            stepsMoreDay.add((int) stepsPointValue.getSteps());
                            Log.d("testingSteps", String.valueOf(stepsPointValue.getSteps()));
                        }else{
                            stepsLessDay.add((int) stepsPointValue.getSteps());
                            Log.d("testingLESSSSSteps", String.valueOf(stepsPointValue.getSteps()));
                        }
                    }
                    if(stepsMoreDay!=null || stepsLessDay!=null) {
                        stepsMOREDays.setText("Number of days with >=7500 steps per day: " + stepsMoreDay.size());
                        stepsLESSDays.setText("Number of days with <7500 steps per day: " + stepsLessDay.size());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
