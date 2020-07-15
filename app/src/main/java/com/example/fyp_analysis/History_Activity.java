package com.example.fyp_analysis;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class History_Activity extends AppCompatActivity {

    private Button onBack;
    private String UserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);

        UserID=getIntent().getExtras().getString("UserID");

        FragmentManager fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("UserID", UserID);
        monthly_history_fragment fragment = new monthly_history_fragment();
        fragment.setArguments(bundle);
        fm.beginTransaction().replace(R.id.activity_container,fragment).commit();

       onBack=findViewById(R.id.onBack3);
        onBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
