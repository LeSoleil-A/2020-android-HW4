package com.example.test_0709_1.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.graphics.Color.GRAY;

public class ClockView extends View {

    private static final int FULL_CIRCLE_DEGREE = 360;
    private static final int UNIT_DEGREE = 6;

    private static final float UNIT_LINE_WIDTH = 8; // 刻度线的宽度
    private static final int HIGHLIGHT_UNIT_ALPHA = 0xFF;
    private static final int NORMAL_UNIT_ALPHA = 0x80;

    private static final float HOUR_NEEDLE_LENGTH_RATIO = 0.4f; // 时针长度相对表盘半径的比例
    private static final float MINUTE_NEEDLE_LENGTH_RATIO = 0.6f; // 分针长度相对表盘半径的比例
    private static final float SECOND_NEEDLE_LENGTH_RATIO = 0.8f; // 秒针长度相对表盘半径的比例
    private static final float HOUR_NEEDLE_WIDTH = 12; // 时针的宽度
    private static final float MINUTE_NEEDLE_WIDTH = 8; // 分针的宽度
    private static final float SECOND_NEEDLE_WIDTH = 4; // 秒针的宽度

    private Calendar calendar = Calendar.getInstance();

    private float radius = 0; // 表盘半径
    private float centerX = 0; // 表盘圆心X坐标
    private float centerY = 0; // 表盘圆心Y坐标

    private List<RectF> unitLinePositions = new ArrayList<>();
    private List<Pair<Float, Float>> unitPointPositions = new ArrayList<>();
    private Paint unitPaint = new Paint();
    private Paint needlePaint_hour = new Paint();
    private Paint needlePaint_minute = new Paint();
    private Paint needlePaint_second = new Paint();
    private Paint numberPaint = new Paint();
    private Paint CenterPaint_s = new Paint();
    private Paint CenterPaint_c = new Paint();

    private Handler handler = new Handler();

    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        unitPaint.setAntiAlias(true);
        unitPaint.setColor(Color.WHITE);
        unitPaint.setStrokeWidth(UNIT_LINE_WIDTH);
        unitPaint.setStrokeCap(Paint.Cap.SQUARE);
        unitPaint.setStyle(Paint.Style.STROKE);

        CenterPaint_s.setAntiAlias(true);
        CenterPaint_s.setColor(Color.WHITE);
        CenterPaint_s.setStyle(Paint.Style.FILL_AND_STROKE);
        CenterPaint_s.setStrokeWidth(6);
        CenterPaint_c.setAntiAlias(true);
        CenterPaint_c.setColor(Color.BLUE);
        CenterPaint_c.setStyle(Paint.Style.FILL);

        // TODO 设置绘制时、分、秒针的画笔: needlePaint
        needlePaint_hour.setAntiAlias(true);
        needlePaint_hour.setColor(Color.WHITE);
        needlePaint_hour.setStrokeWidth(HOUR_NEEDLE_WIDTH);

        needlePaint_minute.setAntiAlias(true);
        needlePaint_minute.setColor(Color.WHITE);
        needlePaint_minute.setStrokeWidth(MINUTE_NEEDLE_WIDTH);

        needlePaint_second.setAntiAlias(true);
        needlePaint_second.setColor(GRAY);
        needlePaint_second.setStrokeWidth(SECOND_NEEDLE_WIDTH);

        // TODO 设置绘制时间数字的画笔: numberPaint
        numberPaint.setAntiAlias(true);
        numberPaint.setColor(Color.WHITE);
        numberPaint.setTextSize(60);
        numberPaint.setStyle(Paint.Style.FILL);
        numberPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        configWhenLayoutChanged();
    }

    private void configWhenLayoutChanged() {
        float newRadius = Math.min(getWidth(), getHeight()) / 2f;
        if (newRadius == radius) {
            return;
        }
        radius = newRadius;
        centerX = getWidth() / 2f;
        centerY = getHeight() / 2f;

        // 当视图的宽高确定后就可以提前计算表盘的刻度线的起止坐标了
        for (int degree = 0; degree < FULL_CIRCLE_DEGREE; degree += UNIT_DEGREE) {
            double radians = Math.toRadians(degree);
            float startX = (float) (centerX + (radius * (1 - 0.05f)) * Math.cos(radians));
            float startY = (float) (centerY + (radius * (1 - 0.05f)) * Math.sin(radians));
            float stopX = (float) (centerX + radius * Math.cos(radians));
            float stopY = (float) (centerY + radius * Math.sin(radians));
            unitLinePositions.add(new RectF(startX, startY, stopX, stopY));
            double radianp = Math.toRadians(degree-60);
            float pointX,pointY;
            if((degree-60) == 0){
                double radianp_0 = Math.toRadians(degree-60+3);
                pointX = (float) (centerX + (radius * (1 - 0.05f) * 0.88f) * Math.cos(radianp_0));
                pointY = (float) (centerY + (radius * (1 - 0.05f) * 0.88f) * Math.sin(radianp_0));
            }
            else if ((degree-60) == 180){
                double radianp_1 = Math.toRadians(degree-60-3);
                pointX = (float) (centerX + (radius * (1 - 0.05f) * 0.88f) * Math.cos(radianp_1));
                pointY = (float) (centerY + (radius * (1 - 0.05f) * 0.88f) * Math.sin(radianp_1));
            }
            else if ((degree-60) >0 && (degree-60)<180){
                pointX = (float) (centerX + (radius * (1 - 0.05f) * 0.92f) * Math.cos(radianp));
                pointY = (float) (centerY + (radius * (1 - 0.05f) * 0.92f) * Math.sin(radianp));
            }
            else{
                pointX = (float) (centerX + (radius * (1 - 0.05f) * 0.85f) * Math.cos(radianp));
                pointY = (float) (centerY + (radius * (1 - 0.05f) * 0.85f) * Math.sin(radianp));
            }
            unitPointPositions.add(new Pair<Float, Float>(pointX, pointY));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawUnit(canvas);
        drawTimeNeedles(canvas);
        drawTimeNumbers(canvas);
        canvas.drawCircle(centerX, centerY, 12, CenterPaint_s);
        canvas.drawCircle(centerX, centerY, 6, CenterPaint_c);

        // TODO 实现时间的转动，每一秒刷新一次
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                invalidate();
                sendEmptyMessageDelayed(1, 1000);
            }
        };
       handler.sendEmptyMessage(1);
    }

    // 绘制表盘上的刻度
    private void drawUnit(Canvas canvas) {
        for (int i = 0; i < unitLinePositions.size(); i++) {
            if (i % 5 == 0) {
                unitPaint.setAlpha(HIGHLIGHT_UNIT_ALPHA);
            } else {
                unitPaint.setAlpha(NORMAL_UNIT_ALPHA);
            }
            RectF linePosition = unitLinePositions.get(i);
            canvas.drawLine(linePosition.left, linePosition.top, linePosition.right, linePosition.bottom, unitPaint);
        }
    }

    private void drawTimeNeedles(Canvas canvas) {
        Time time = getCurrentTime();
        int hour = time.getHours();
        int minute = time.getMinutes();
        int second = time.getSeconds();
        // TODO 根据当前时间，绘制时针、分针、秒针
        /**
         * 思路：
         * 1、以时针为例，计算从0点（12点）到当前时间，时针需要转动的角度
         * 2、根据转动角度、时针长度和圆心坐标计算出时针终点坐标（起始点为圆心）
         * 3、从圆心到终点画一条线，此为时针
         * 注1：计算时针转动角度时要把时和分都得考虑进去
         * 注2：计算坐标时需要用到正余弦计算，请用Math.sin()和Math.cos()方法
         * 注3：Math.sin()和Math.cos()方法计算时使用不是角度而是弧度，所以需要先把角度转换成弧度，
         *     可以使用Math.toRadians()方法转换，例如Math.toRadians(180) = 3.1415926...(PI)
         * 注4：Android视图坐标系的0度方向是从圆心指向表盘3点方向，指向表盘的0点时是-90度或270度方向，要注意角度的转换
         */

        if(hour >= 12)
            hour = hour - 12;
        Log.i("Hour", String.valueOf(hour));
        float hourDegree = hour*30+minute*0.5f - 90;
        Log.i("Minute", String.valueOf(minute));
        float minuteDegree = minute*6+second*0.1f - 90 ;
        Log.i("Second", String.valueOf(second));
        float secondDegree = second*6-90;

        float endX_hour = (float) (centerX + radius * HOUR_NEEDLE_LENGTH_RATIO * Math.cos(Math.toRadians(hourDegree)));
        float endY_hour = (float) (centerY + radius * HOUR_NEEDLE_LENGTH_RATIO * Math.sin(Math.toRadians(hourDegree)));
        float endX_minute = (float) (centerX + radius * MINUTE_NEEDLE_LENGTH_RATIO * Math.cos(Math.toRadians(minuteDegree)));
        float endY_minute = (float) (centerY + radius * MINUTE_NEEDLE_LENGTH_RATIO * Math.sin(Math.toRadians(minuteDegree)));
        float endX_second = (float) (centerX + radius * SECOND_NEEDLE_LENGTH_RATIO * Math.cos(Math.toRadians(secondDegree)));
        float endY_second = (float) (centerY + radius * SECOND_NEEDLE_LENGTH_RATIO * Math.sin(Math.toRadians(secondDegree)));

        canvas.drawLine(centerX, centerY, endX_hour, endY_hour, needlePaint_hour);
        canvas.drawLine(centerX, centerY, endX_minute, endY_minute, needlePaint_minute);
        canvas.drawLine(centerX, centerY, endX_second, endY_second, needlePaint_second);
    }

    private void drawTimeNumbers(Canvas canvas) {
        // TODO 绘制表盘时间数字（可选）
        for (int i = 0; i < 12; i++) {
            String number = "";

            if( i+1<10 )
                number = "0" + String.valueOf(i+1);
            else
                number = String.valueOf(i+1);

            Pair<Float, Float> point = unitPointPositions.get(i*5);
            canvas.drawText( number, point.first, point.second, numberPaint);
        }
    }

    // 获取当前的时间：时、分、秒
    private Time getCurrentTime() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return new Time(
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));
    }
}
