package ch.ethz.vs.se.team7.carbonfootprintmonitor;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

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

    private float[] yData;
    private String[] xData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        FrameLayout frameLayout= (FrameLayout) findViewById(R.id.chartContainer);
        pieChart = new com.github.mikephil.charting.charts.PieChart(this);

        frameLayout.addView(pieChart);

        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);


        List<List<String>> values = new SQLQueryHelper(this).getRecordsStringArray(GET_PIE_CHART_DATA);
        yData = new float[values.size()];
        xData = new String[values.size()];
        int ycount = 0;
        int xcount = 0;
        for(int i = 0; i < values.size(); i++)
        {
            yData[ycount++] = Integer.parseInt(values.get(i).get(0));
            switch (Integer.parseInt(values.get(i).get(1))){
                case DetectedActivity.ON_BICYCLE:
                    xData[xcount++] = "Bicycle";
                    break;
                case DetectedActivity.ON_FOOT:
                    xData[xcount++] = "On Foot";
                    break;
                case DetectedActivity.WALKING:
                    xData[xcount++] = "Walking";
                    break;
                case DetectedActivity.IN_VEHICLE:
                    xData[xcount++] = "In Vehicle";
                    break;
                case DetectedActivity.RUNNING:
                    xData[xcount++] = "Running";
                    break;
                case DetectedActivity.TILTING:
                    xData[xcount++] = "Tilting";
                    break;
                case DetectedActivity.STILL:
                    xData[xcount++] = "Standing Still";
                    break;
                case DetectedActivity.UNKNOWN:
                    xData[xcount++] = "Unknown";
                    break;

            }
        }

        addData();

        Legend legend = pieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setXEntrySpace(7);
        legend.setYEntrySpace(5);


    }

    private void addData(){
        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();

        for (int i = 0; i < yData.length; i++)
            yVals1.add(new PieEntry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        PieDataSet dataSet = new PieDataSet(yVals1, "Your Travel Info");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

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
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }
}

