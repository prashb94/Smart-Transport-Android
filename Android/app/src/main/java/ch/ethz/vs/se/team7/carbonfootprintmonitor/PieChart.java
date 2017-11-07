package ch.ethz.vs.se.team7.carbonfootprintmonitor;

/**
 * Created by Prashanth on 11/5/2017.
 */

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.SQLQueryHelper;

import static ch.ethz.vs.se.team7.carbonfootprintmonitor.Storage.SQLQueries.GET_PIE_CHART_DATA;

public class PieChart extends AppCompatActivity {

    private com.github.mikephil.charting.charts.PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        FrameLayout frameLayout= (FrameLayout) findViewById(R.id.chartContainer);
        pieChart = new com.github.mikephil.charting.charts.PieChart(this);
        //Get FrameLayout
        frameLayout.addView(pieChart);
        //Settings to make the pie chart look nicer
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);

        List<PieEntry> entries = new ArrayList<>();
        //Aggregate data and get 2D List of values. Convert each to a pie chart entry. Fallthrough for useless cases.
        List<List<String>> values = new SQLQueryHelper(this).getRecordsStringArray(GET_PIE_CHART_DATA);
        for(int i = 0; i < values.size(); i++)
        {
            float val1 = Float.parseFloat(values.get(i).get(0) + "f");
            switch (Integer.parseInt(values.get(i).get(1))){
                case DetectedActivity.ON_BICYCLE:
                    entries.add(new PieEntry(val1, "Bicycle"));
                    break;
                case DetectedActivity.ON_FOOT:
                case DetectedActivity.WALKING:
                    entries.add(new PieEntry(val1, "Walking"));
                    break;
                case DetectedActivity.IN_VEHICLE:
                    entries.add(new PieEntry(val1, "Vehicle"));
                    break;
                case DetectedActivity.RUNNING:
                    entries.add(new PieEntry(val1, "Running"));
                    break;
                case DetectedActivity.TILTING:
                case DetectedActivity.STILL:
                    entries.add(new PieEntry(val1, "Standing Still"));
                    break;
                case DetectedActivity.UNKNOWN:
                    entries.add(new PieEntry(val1, "Unknown"));
                    break;
            }
        }
        //Create dataset and set chart colors
        PieDataSet set = new PieDataSet(entries, "");
        set.setSliceSpace(3);
        set.setSelectionShift(5);
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        set.setColors(colors);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setTextColor(Color.BLACK);
        l.setYOffset(0f);

        //Set true to enable legend
        l.setEnabled(true);

        //Add data to chart, set text colors and animation
        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(10f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(10f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        pieChart.animateXY(2000,2000);
        //Refresh chart
        pieChart.invalidate();


    }

}

