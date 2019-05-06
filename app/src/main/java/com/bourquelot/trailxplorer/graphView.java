package com.bourquelot.trailxplorer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class graphView extends View {

    private int pxPerUnit;
    private ArrayList<Integer> compSpeedArr;
    private List<Integer>  timeArr;
    private Paint paint;
    private Rect rect;
    private Paint pointPaint;
    private Paint textPaint;
    private List<Point> pointList;
    private int zeroY;
    static int padding = 60;
    static int paddingTop = 10;

    public graphView(Context context){
        super(context);
        init();
    }

    public graphView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    public graphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorPrimaryDark));
        paint.setStyle(Paint.Style.STROKE);
        pointPaint = new Paint();
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(getResources().getColor(R.color.colorPrimary));
        textPaint = new Paint();
        textPaint.setTextSize(50);
        textPaint.setColor(getResources().getColor(R.color.colorPrimaryDark));
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        generateArrays();
        calcPositions();

        drawLine(canvas);
        drawGraduations(canvas);
    }
    private void calcPositions(){
        pointList = new ArrayList<>();
        int max = compSpeedArr.get(0);
        int min = max;
        for (int f:compSpeedArr) {
            if(f > max){
                max = f;
            }
            if(f < min){
                min = f;
            }
        }
//        Log.d("max", String.valueOf(max));
//        Log.d("min", String.valueOf(min));
//        Log.d("max height", String.valueOf(getHeight()));
        pxPerUnit = (getHeight()) / (max - min);
//        Log.d("pxPerUnit", String.valueOf(pxPerUnit));
        zeroY = Math.round(max * pxPerUnit + paddingTop);
//        Log.d("ZERO", String.valueOf(zeroY));
        int step = (getWidth() - 2 * padding)/(compSpeedArr.size() - 1);
        for (int i = 0; i < compSpeedArr.size(); i++) {
            int x = step * i + padding;
            int y = zeroY - Math.round(compSpeedArr.get(i)) * pxPerUnit;
            pointList.add(new Point(x, y));
        }
//        Log.d("lol", pointList.toString());
    }
    private void generateArrays(){
        List<Location> larray = arraygpx.getlocationArray();
        compSpeedArr = new ArrayList<>();
        compSpeedArr.add(0);
        timeArr = new ArrayList<>();
        timeArr.add(0);
        long baseTime = larray.get(0).getElapsedRealtimeNanos();
        for (int i = 1; i < larray.size(); i++) {
            int timeTmp = (int) Math.round((larray.get(i).getElapsedRealtimeNanos() - baseTime) / 1_000_000_000.0);
            timeArr.add(timeTmp);
            compSpeedArr.add(Math.round(larray.get(i).distanceTo(larray.get(i-1))/(larray.get(i).getElapsedRealtimeNanos() - larray.get(i - 1).getElapsedRealtimeNanos())* 1_000_000_000.0f *3.6f));
        }
//        Log.d("str", timeArr.toString());
//        Log.d("str", compSpeedArr.toString());
    }
    private void drawGraduations(Canvas canvas){
        int x = pointList.get(pointList.size() - 1).x + Math.round(padding*0.15f);
        int[] graduations = new int[]{5, 10, 15, 20, 25, 30, 35, 40};
        for(int value: graduations){
            int y = Math.round(zeroY - value*1f * pxPerUnit);
            String format = NumberFormat.getInstance().format(value);
            canvas.drawText(format, x, y, textPaint);
        }
    }
    private void drawLine(Canvas canvas){
        Point previousPoint = null;
        for(Point p:pointList){
            if(previousPoint != null){
                //draw a line
                Point p1 = previousPoint;
                Point p2 = p;
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
            }
            previousPoint = p;
            //draw mark
            canvas.drawCircle(
                    p.x,
                    p.y,
                    12,
                    pointPaint
            );

        }
    }
}
