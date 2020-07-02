package com.example.fyp_analysis;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class User_Step_Only extends AppCompatActivity {

    private String userDate, userID;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference stepDatabaseRef;
    ArrayList<String> stepsdataarray;
    ArrayList<String> stepsdate= new ArrayList<String>();
    ArrayList<String> stepsvalue = new ArrayList<String>();

    String[] stepsArray = { "test1", "test2"};
    List<String> mylist;
    LineGraphSeries<DataPoint> stepsg;
    //Date x;
    int x;
    int y;

    private RecyclerView mrecyclerView;
    private RecyclerView.LayoutManager mlayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_step_only);
        firebaseDatabase= FirebaseDatabase.getInstance();


        userDate = getIntent().getExtras().getString("UserDate");
        userID=getIntent().getExtras().getString("UserID");
        Log.d("UserID ", String.valueOf( userID));

        getUserSteps();
        Log.d("stepsdate size", "SIZE2" + stepsdate.size() );
        for (int i = 0; i < stepsdate.size(); i++) {
            Log.d("SPLIT2", "date" + stepsdate.get(i) + "Steps" + stepsvalue.get(i));
        }
       // ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_step_listview,mylist);
        //ListView listView = (ListView)findViewById(R.id.steps_list);
       // listView.setAdapter(adapter);
        /*GraphView stepgraph = (GraphView)findViewById(R.id.Stepsgraph);
        stepsg = new LineGraphSeries<DataPoint>();
        for (int i = 0; i < stepsdate.size(); i++) {
            x = Integer.parseInt(stepsdate.get(i));
            y = Integer.parseInt(stepsvalue.get(i));
            stepsg.appendData(new DataPoint (x,y),true,stepsdate.size());
        }
        stepgraph.addSeries(stepsg); */



    }
    public void getUserSteps(){
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Steps Count/" + userID );
        databaseReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                String child = dataSnapshot.getValue().toString();

                    Log.d("Datasnapshot", child);
                    stepsArray = child.split(",");
                    Log.d("stepsArray", stepsArray.toString());
                    child = child.replace("{", "");
                    child = child.replace("}", "");
                    child = child.replace("=steps=", ";");
                    stepsdataarray = new ArrayList<>(Arrays.asList(child.split(",")));
                    stepsdate = new ArrayList<String>();
                    stepsvalue = new ArrayList<String>();
                    for (int i = 0; i < stepsdataarray.size(); i++) {
                        Log.d("SPLIT", "Beforevalue" + stepsdataarray.get(i));
                        String indexsteps = stepsdataarray.get(i);
                        String[] splitvalue = indexsteps.split(";");
                        stepsdate.add(splitvalue[0]);  //date
                        stepsvalue.add(splitvalue[1]);  //value
                        Log.d("SPLIT1", "date" + stepsdate.get(i) + "Steps" + stepsvalue.get(i));
                    }
                    Log.d("stepsdate size", "SIZE " + stepsdate.size() );
                    GraphView stepgraph = (GraphView)findViewById(R.id.Stepsgraph);
                    stepsg = new LineGraphSeries<DataPoint>();
                    for (int i = 0; i < stepsdate.size(); i++) {
                        //x = new Date(Integer.parseInt(stepsdate.get(i)));
                        //x = Integer.parseInt(stepsdate.get(i));
                        x=i;
                        y = Integer.parseInt(stepsvalue.get(i));
                        stepsg.appendData(new DataPoint (x,y),true,stepsdate.size());
                    }
                    stepgraph.addSeries(stepsg);
                   // stepgraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(User_Step_Only.this));
                  //  stepgraph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

                   // stepgraph.getViewport().setMinX(02052020);
                   // stepgraph.getViewport().setMaxX(22052020);
                    //stepgraph.getViewport().setXAxisBoundsManual(true);

                    //stepgraph.getGridLabelRenderer().setHumanRounding(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(User_Step_Only.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
