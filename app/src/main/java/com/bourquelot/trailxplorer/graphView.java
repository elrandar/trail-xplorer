package com.bourquelot.trailxplorer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class graphView extends View {

    private List<Float> speedArr;
    private Paint paint;
    private Rect rect;
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

    private void generateSpeedArray(){
        List<Location> larray = arraygpx.getlocationArray();

    }


}
