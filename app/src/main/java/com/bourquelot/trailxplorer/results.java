package com.bourquelot.trailxplorer;

import android.content.Intent;
import android.graphics.Canvas;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class results extends AppCompatActivity {

    graphView graphView;
    public TextView tv1;
    public TextView tv2;
    public TextView tv3;
    public TextView tv4;
    private List<Location> lArray;
    private double timeTaken;
    private float totalDistance;
    private float averageSpeed;
    private double maximumAltitude;
    private double minimumAltitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        tv1 = findViewById(R.id.result1);
        tv2 = findViewById(R.id.result2);
        tv3 = findViewById(R.id.result3);
        tv4 = findViewById(R.id.results4);
        TextView tv5 = findViewById(R.id.result5);
        lArray = arraygpx.getlocationArray();

        timeTaken = getTimeTaken();
        Date d = new Date(Math.round(timeTaken) * 1000L);
        SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("UTC"));
        String time = dff.format(d);

        totalDistance = getTotalDistance();
        averageSpeed = getAverageSpeed();
        getAltitudes();

        DecimalFormat df = new DecimalFormat("######.#");

        tv1.setText(time);
        tv2.setText(df.format(totalDistance) + " m");
        tv3.setText(df.format(averageSpeed) + " m/s");
        tv4.setText(df.format(maximumAltitude) + " m");
        tv5.setText(df.format(minimumAltitude) + " m");

        graphView = findViewById(R.id.graphView);
    }

    @Override
    public void onBackPressed(){
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
    }
    private Float getAverageSpeed(){

        return totalDistance/(float)timeTaken;
    }

    private double getTimeTaken(){
        long deltaTime = lArray.get(lArray.size()-1).getElapsedRealtimeNanos() - lArray.get(0).getElapsedRealtimeNanos();
        double seconds = (double)deltaTime / 1_000_000_000.0;
        return seconds;

    }

    private float getTotalDistance(){
        float sum = 0f;
        for (int i = 0; i < lArray.size() - 1; i++) {
            float distance = lArray.get(i).distanceTo(lArray.get(i+1));
            sum += distance;
        }
        return sum;
    }

    private void getAltitudes(){
        double max = 0;
        double min = lArray.get(0).getAltitude();
        for(int i = 0; i < lArray.size(); i++){
            double currAltitude = lArray.get(i).getAltitude();
            if(currAltitude > max){
                max = currAltitude;
            }
            if(currAltitude < min){
                min = currAltitude;
            }
        }
        minimumAltitude = min;
        maximumAltitude = max;
    }
}
