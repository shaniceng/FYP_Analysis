package com.example.fyp_analysis;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class User_Step_Only extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {

    private String daymonth01, daymonth02,daymonth03,daymonth04,daymonth05,daymonth06,daymonth07,daymonth08,daymonth09,daymonth10,daymonth11,daymonth12,daymonth13,daymonth14,daymonth15,daymonth16,daymonth17,daymonth18,
            daymonth19,daymonth20,daymonth21,daymonth22,daymonth23,daymonth24,daymonth25,daymonth26,daymonth27,daymonth28,daymonth29,daymonth30,daymonth31;
    private int maxday,maxmonth,dayselected,monthselected;
    private String steps,date;
    private String userDate, userID;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference stepDatabaseRef;
    private Button onBack,day_steps_btn,month_steps_btn,all_steps_btn;
    private TextView selecteddate,daysteps;
    ArrayList<String> stepsdataarray;
    ArrayList<String> stepsdate= new ArrayList<String>();
    ArrayList<String> stepsvalue = new ArrayList<String>();

    LineGraphSeries<DataPoint> stepsg;
    int x;
    int y;
    int d1,b1,c1;
    int minI,maxI;
    Date d,b,c,min,max;
    String datadate,selecteddaysteps;


    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public User_Step_Only() throws ParseException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_step_only);
        firebaseDatabase= FirebaseDatabase.getInstance();

        selecteddate=findViewById(R.id.selected_date_tv);
        daysteps=findViewById(R.id.day_steps_tv);

        userDate = getIntent().getExtras().getString("UserDate");
        userID=getIntent().getExtras().getString("UserID");
        Log.d("UserID ", String.valueOf( userID));

        onBack=findViewById(R.id.onBack);
        onBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        all_steps_btn=findViewById(R.id.all_steps_btn);
        all_steps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                daysteps.setText("");
                getUserAllSteps();
                selecteddate.setText("Show all of user's step data");
            }
        });

        month_steps_btn=findViewById(R.id.month_steps_btn);
        month_steps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthselected=1;
                showDatePickerDailog();
            }
        });

        day_steps_btn=findViewById(R.id.day_steps_btn);
        day_steps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayselected =1;
                showDatePickerDailog();
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        if (dayselected ==1){
            Toast.makeText(getApplicationContext(),"ondataset :",Toast.LENGTH_SHORT).show();
             month = month +1;
             date = year+ "/" + month +"/" +  dayOfMonth;
             datadate =  year + String.format("%02d", month)  +String.format("%02d", dayOfMonth) ;
             selecteddate.setText(date);
             getUserDaySteps();
        }
        if (monthselected ==1){
            String date = year+ "/" + month ;    
            MonthDateLimit(year, month, dayOfMonth);
            selecteddate.setText(date);
            getUserMonthSteps();

        }
        dayselected=0;
        monthselected=0;

    }

    public void MonthDateLimit( int year, int month, int dayOfMonth){
        month = month + 1;
        dayOfMonth=1;
        if (month == 1||month == 3||month == 5||month == 7||month == 8||month == 10||month == 12){ maxday=31+1; }
        else if (month == 2){
            if(((year%4==0) && (year%100!=0) || (year%400==0))) { maxday = 29+1; }
            else { maxday = 28 + 1; }
        }
        else{ maxday = 30+1; }
        maxmonth = 12+1;
        String startdate = "01"+ String.format("%02d", month) + year;
        String enddate = maxday+String.format("%02d", month) + year;

        daymonth01 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth02 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth03 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth04 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth05 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth06 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth07 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth08 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth09 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth10 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth11 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth12 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth13 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth14 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth15 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth16 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth17 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth18 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth19 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth20 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth21 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth22 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth23 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth24 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth25 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth26 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth27 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        daymonth28 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
        dayOfMonth = dayOfMonth+1;
        if ( dayOfMonth != maxday) {
            daymonth29 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
            dayOfMonth = dayOfMonth + 1;
            if ( dayOfMonth != maxday) {
                daymonth30 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
                dayOfMonth = dayOfMonth + 1;
                if ( dayOfMonth != maxday) {
                    daymonth31 = year +  String.format("%02d", month) + String.format("%02d", dayOfMonth);
                }
                else { daymonth31 = "0";}
            }
            else { daymonth30 = "0";}
        }
        else { daymonth29 = "0";}
    }

    public void showGraph(){
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

       private void showDatePickerDailog(){
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    this,
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }

    public void getUserAllSteps(){

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
                    showGraph();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(User_Step_Only.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }//end getUserAllSteps

    public void getUserDaySteps(){

        if (datadate != null) {

            stepDatabaseRef=FirebaseDatabase.getInstance().getReference();
            stepDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child("Steps Count").child(userID).child(datadate).hasChild("steps")){
                        selecteddaysteps = dataSnapshot.child("Steps Count").child(userID).child(datadate).child("steps").getValue().toString(); }
                    else { selecteddaysteps = "0"; }

                    daysteps.setText("Steps: " + selecteddaysteps);

                    stepsdate = new ArrayList<String>();
                    stepsvalue = new ArrayList<String>();
                    stepsdate.add(datadate);  //date
                    stepsvalue.add(selecteddaysteps);  //value
                    showGraph();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
    }//end getUserDaySteps

    public void getUserMonthSteps() {

            stepDatabaseRef = FirebaseDatabase.getInstance().getReference();
            stepDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    stepsdate = new ArrayList<String>();
                    stepsvalue = new ArrayList<String>();

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth01).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth01).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth01);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth02).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth02).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth02);  //date
                    stepsvalue.add(steps);  //value


                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth03).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth03).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth03);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth04).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth04).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth04);  //date
                    stepsvalue.add(steps);  //value


                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth05).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth05).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth05);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth06).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth06).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth06);  //date
                    stepsvalue.add(steps);  //value


                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth07).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth07).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth07);  //date
                    stepsvalue.add(steps);  //value


                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth08).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth08).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth08);  //date
                    stepsvalue.add(steps);  //value


                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth09).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth09).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth09);  //date
                    stepsvalue.add(steps);  //value


                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth10).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth10).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth10);  //date
                    stepsvalue.add(steps);  //value


                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth11).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth11).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth11);  //date
                    stepsvalue.add(steps);  //value


                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth12).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth12).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth12);  //date
                    stepsvalue.add(steps);  //value


                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth13).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth13).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth13);  //date
                    stepsvalue.add(steps);  //value


                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth14).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth14).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth14);  //date
                    stepsvalue.add(steps);  //value


                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth15).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth15).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth15);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth16).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth16).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth16);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth17).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth17).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth17);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth18).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth18).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth18);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth19).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth19).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth19);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth20).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth20).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth20);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth21).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth21).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth21);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth22).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth22).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth22);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth23).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth23).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth23);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth24).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth24).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth24);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth25).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth25).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth25);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth26).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth26).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth26);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth27).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth27).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth27);  //date
                    stepsvalue.add(steps);  //value

                    if (dataSnapshot.child("Steps Count").child(userID).child(daymonth28).hasChild("steps")) {
                        steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth28).child("steps").getValue().toString();
                    } else {
                        steps = "0";
                    }
                    stepsdate.add(daymonth28);  //date
                    stepsvalue.add(steps);  //value

                    if (daymonth29 != "0") {
                        if (dataSnapshot.child("Steps Count").child(userID).child(daymonth29).hasChild("steps")) {
                            steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth29).child("steps").getValue().toString();
                        } else {
                            steps = "0";
                        }
                        stepsdate.add(daymonth29);  //date
                        stepsvalue.add(steps);  //value
                    }

                    if (daymonth30 != "0") {
                        if (dataSnapshot.child("Steps Count").child(userID).child(daymonth30).hasChild("steps")) {
                            steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth30).child("steps").getValue().toString();
                        } else {
                            steps = "0";
                        }
                        stepsdate.add(daymonth30);  //date
                        stepsvalue.add(steps);  //value
                    }

                    if (daymonth31 != "0") {
                        if (dataSnapshot.child("Steps Count").child(userID).child(daymonth31).hasChild("steps")) {
                            steps = dataSnapshot.child("Steps Count").child(userID).child(daymonth31).child("steps").getValue().toString();
                        } else {
                            steps = "0";
                        }
                        stepsdate.add(daymonth31);  //date
                        stepsvalue.add(steps);  //value
                    }

                    for (int i = 0; i < stepsdate.size(); i++) {
                        Log.d("MONTHDATE", "month_date" + stepsdate.get(i) + "Steps" + stepsvalue.get(i));
                    }
                    showGraph();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

    }

}//end activity

