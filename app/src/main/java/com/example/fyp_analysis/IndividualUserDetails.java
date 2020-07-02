package com.example.fyp_analysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

public class IndividualUserDetails extends AppCompatActivity implements MyUserDetailsAdapter.OnDateListener{

    private TextView userID, userName, userAge, userEmail;
    private FirebaseDatabase firebaseDatabase;
    private String user;
    private Button onBack,Stepbtn;
    private ArrayList<String>userDate;

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
        mrecyclerView = findViewById(R.id.UserTrackerRecyclerVew);
        InsertRecyclerView();
        onBack=findViewById(R.id.onBack);
        onBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        userID.setText(user);

        Stepbtn=findViewById(R.id.steps_btn);
        Stepbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IndividualUserDetails.this, User_Step_Only.class);
                intent.putExtra("UserID", user);
                startActivity(intent);
            }
        });
        getUserDetails();
        getUserDaily();
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(IndividualUserDetails.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getUserDaily(){
        userDate=new ArrayList<>();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Steps Count/" + user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {
//                        UserDetails userDetails = myDataSnapshot.getValue(UserDetails.class);
                        userDate.add(myDataSnapshot.getKey());
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
}
