package com.example.fyp_analysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class User_Activity_Track extends AppCompatActivity {

    private String userDate, userID;
    private TextView UserDate, UserSteps;
    private Button onBack;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user___track);
        firebaseDatabase= FirebaseDatabase.getInstance();
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
        UserDate.setText(userDate);
        getUserSteps();
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
}
