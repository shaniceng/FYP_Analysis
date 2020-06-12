package com.example.fyp_analysis;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverallFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PieChart control_steps_chart;

    public OverallFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverallFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverallFragment newInstance(String param1, String param2) {
        OverallFragment fragment = new OverallFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overall, container, false);
        control_steps_chart = view.findViewById(R.id.control_steps_piechart);
        
        setupPieControlStepsChart();
        
        return view;
    }

    private void setupPieControlStepsChart() {

        List<PieEntry> pieEntryList = new ArrayList<>();

        //insert entries

        PieDataSet dataSet = new PieDataSet(pieEntryList, "Steps Count");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData=new PieData(dataSet);

        //get the chart
        control_steps_chart.setData(pieData);
        control_steps_chart.animateY(1000);
        control_steps_chart.invalidate();
    }

    private void getStepsCountIntervention(){
        // collect all users's steps count
        //check user id if is control or intervention group
        //if control, check steps count data
    }
}
