package com.example.fyp_analysis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class User_Activity_Track extends AppCompatActivity {

    private String userDate;
    private TextView UserDate, UserSteps;
    private Button onBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user___track);
        userDate = getIntent().getExtras().getString("UserDate");
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
    }
}
