package com.bourquelot.trailxplorer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class graphView extends View {

    private ArrayList<Float> compSpeedArr;
    private List<Double>  timeArr;
    private Paint paint;
    private Rect rect;
    private List<Point> pointList;
    static int padding = 8;

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
        rect = new Rect();
        paint.setColor(Color.BLACK);

        generateArrays();
        //calcPositions();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rect.left = padding;
        rect.right = getWidth() - padding;
        rect.bottom = getHeight() - padding;
        rect.top = padding;

        canvas.drawRect(rect, paint);

    }
    private void calcPositions(){
        float max = compSpeedArr.get(0);
        float min = max;
        for (float f:compSpeedArr) {
            if(f > max){
                max = f;
            }
            if(f < min){
                min = f;
            }
        }
        int pxPerUnit = Math.round(getHeight() / (max - min));
        int zeroY = Math.round(max * pxPerUnit + padding);
        int step = (getWidth() - 2 * padding)/(compSpeedArr.size() - 1);
        for (int i = 0; i < compSpeedArr.size(); i++) {
            int x = step * i + padding;
            int y = zeroY - Math.round(compSpeedArr.get(i)) * pxPerUnit;
            pointList.add(new Point(x, y));
        }
        Log.d("lol", pointList.toString());
    }
    private void generateArrays(){
        List<Location> larray = arraygpx.getlocationArray();
        compSpeedArr = new ArrayList<>();
        compSpeedArr.add(0f);
        timeArr = new ArrayList<>();
        timeArr.add(0d);
        long baseTime = larray.get(0).getElapsedRealtimeNanos();
        for (int i = 1; i < larray.size(); i++) {
            timeArr.add((larray.get(i).getElapsedRealtimeNanos() - baseTime) / 1_000_000_000.0);
            compSpeedArr.add(larray.get(i).distanceTo(larray.get(i-1))/(larray.get(i).getElapsedRealtimeNanos() - larray.get(i - 1).getElapsedRealtimeNanos())* 1_000_000_000.0f);
        }
        Log.d("str", timeArr.toString());
        Log.d("str", compSpeedArr.toString());
    }


}
