package com.example.fyp_analysis;

import android.graphics.Color;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DecimalFormat;
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
    private Button onBack;
    ArrayList<String> stepsdataarray;
    ArrayList<String> stepsdate= new ArrayList<String>();
    ArrayList<String> stepsvalue = new ArrayList<String>();


    String[] stepsArray = { "test1", "test2"};
    List<String> mylist;
    LineGraphSeries<DataPoint> stepsg;
    int x;
    int y;
    int d1,b1,c1;
    int minI,maxI;
    Date d,b,c,min,max;

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

        onBack=findViewById(R.id.onBack);
        onBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        userDate = getIntent().getExtras().getString("UserDate");
        userID=getIntent().getExtras().getString("UserID");
        Log.d("UserID ", String.valueOf( userID));

        getUserSteps();


    }
    public void getUserSteps(){


        final DatabaseReference databaseReference = firebaseDatabase.getReference("Steps Count/" + userID );
        Query dataOrderedByKey = databaseReference.orderByKey();
        dataOrderedByKey.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    ArrayList<String> stepschildlist = new ArrayList<String>();

                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                        String child2 =childSnapshot.toString();
                        child2 = child2.replace("DataSnapshot { key = ","");
                        child2 = child2.replace(" value = {steps=","");
                        child2 = child2.replace("}","");
                        child2 = child2.replace(" ","");
                        stepschildlist.add(child2);
                    }

                    for (int i = 0; i < stepschildlist.size(); i++) {
                        Log.d("stepschildlist",  stepschildlist.get(i)); }

                    stepsdate = new ArrayList<String>();
                    stepsvalue = new ArrayList<String>();
                    for (int i = 0; i < stepschildlist.size(); i++) {
                        String indexsteps = stepschildlist.get(i);
                        String[] splitvalue = indexsteps.split(",");
                        stepsdate.add(splitvalue[0]);  //date
                        stepsvalue.add(splitvalue[1]);  //value
                        Log.d("SPLIT", "date" + stepsdate.get(i) + "Steps" + stepsvalue.get(i));
                    }
                    Log.d("stepsdate size", "SIZE " + stepsdate.size() );

                    GraphView stepgraph = (GraphView)findViewById(R.id.Stepsgraph);

                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");


                    stepsg = new LineGraphSeries<DataPoint>();
                    for (int i = 0; i < stepsdate.size(); i++) {

                        try {
                            b = sdf.parse(stepsdate.get(i));
                        } catch (ParseException ex) {
                            Log.v("Exception", ex.getLocalizedMessage());
                        }

                       if(i==0){
                           try {
                           min = sdf.parse(stepsdate.get(i));
                           } catch (ParseException ex) {
                           Log.v("Exception", ex.getLocalizedMessage()); }
                       }
                        if(i==stepsdate.size()-1){
                            try {
                                max = sdf.parse(stepsdate.get(i));
                            } catch (ParseException ex) {
                                Log.v("Exception", ex.getLocalizedMessage()); }
                        }

                        y = Integer.parseInt(stepsvalue.get(i));
                        stepsg.appendData(new DataPoint (b,y),true,stepsdate.size());
                           // new DataPoint(x, y);
                    }


                    stepgraph.removeAllSeries();
                    stepgraph.addSeries(stepsg);


                    stepgraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
                    {
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            if(isValueX){
                                return sdf.format(new Date((long)value));
                            }
                            else{
                                return super.formatLabel(value, isValueX);
                            }
                        }
                    });

                    stepgraph.setTitle("Steps");
                    stepgraph.getViewport().setMinX(min.getTime()-10800000);
                    stepgraph.getViewport().setMaxX(max.getTime());
                    stepgraph.getViewport().setMinY(0);
                    stepgraph.getViewport().setMaxY(10000);
                    stepgraph.getViewport().setYAxisBoundsManual(true);
                    stepgraph.getViewport().setXAxisBoundsManual(true);
                    stepgraph.getViewport().setScrollable(true);
                    stepgraph.getViewport().setScalable(true);

                    stepsg.setDrawBackground(true);

                    stepgraph.setBackgroundColor(Color.argb(100, 163, 180, 195));
                    stepsg.setColor(Color.argb(255, 0, 51, 102));
                    stepsg.setDrawDataPoints(true);
                    stepsg.setDataPointsRadius(15);
                    stepsg.setThickness(10);
                    stepgraph.getGridLabelRenderer().setNumHorizontalLabels(3);
                    stepsg.setOnDataPointTapListener(new OnDataPointTapListener() {
                        @Override
                        public void onTap(Series series, DataPointInterface dataPoint) {
                            Date d = new java.sql.Date((long) dataPoint.getX());
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
                            String formatted = format1.format(d.getTime());
                            int IntValueY = (int) dataPoint.getY();
                            Toast.makeText(User_Step_Only.this, "Steps: "+IntValueY + "  Date: " +formatted, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(User_Step_Only.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
