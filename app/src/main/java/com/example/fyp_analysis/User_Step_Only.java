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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
    int x;
    int y;
    Date d,b,c;

    private RecyclerView mrecyclerView;
    private RecyclerView.LayoutManager mlayoutManager;
    private RecyclerView.Adapter mAdapter;

    public User_Step_Only() throws ParseException {
    }

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
                    //stepsg = new LineGraphSeries<DataPoint>();
                   /* for (int i = 0; i < stepsdate.size(); i++) {
                        //x = new Date(Integer.parseInt(stepsdate.get(i)));
                       // x = Integer.parseInt(stepsdate.get(i));
                        //y = Integer.parseInt(stepsvalue.get(i));
                        stepsg.appendData(new DataPoint (x,y),true,stepsdate.size());
                    }*/

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    try {
                         d = sdf.parse("20200601");
                    } catch (ParseException ex) {
                        Log.v("Exception", ex.getLocalizedMessage());
                    }
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
                    try {
                        b = sdf2.parse("20200602");
                    } catch (ParseException ex) {
                        Log.v("Exception", ex.getLocalizedMessage());
                    }
                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
                    try {
                        c = sdf3.parse("20200603");
                    } catch (ParseException ex) {
                        Log.v("Exception", ex.getLocalizedMessage());
                    }

                   // Integer value3 = 20200603;
                    //int year3 = value3 / 10000;
                    //int month3 = (value3 % 10000) / 100;
                    //int day3 = value3 % 100;
                    //Date d3 = new GregorianCalendar(2020, 06, 03).getTime();

                    LineGraphSeries<DataPoint> stepsg = new LineGraphSeries<>(new DataPoint[] {
                            new DataPoint(d, 1),
                            new DataPoint(b, 3),
                            new DataPoint(c, 2)
                    });

                    stepgraph.addSeries(stepsg);

                    stepgraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(User_Step_Only.this));
                    stepgraph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space

                    stepgraph.getViewport().setMinX(d.getTime());
                    stepgraph.getViewport().setMaxX(c.getTime());
                    stepgraph.getViewport().setMinY(0);
                    stepgraph.getViewport().setYAxisBoundsManual(true);
                    stepgraph.getViewport().setXAxisBoundsManual(true);

                    //stepgraph.getViewport().setScalable(true);  // activate horizontal zooming and scrolling
                    stepgraph.getViewport().setScrollable(true);  // activate horizontal scrolling
                    //stepgraph.getViewport().setScalableY(true);  // activate horizontal and vertical zooming and scrolling
                    //stepgraph.getViewport().setScrollableY(true);  // activate vertical scrolling

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
