package com.example.administrator.testcalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2017\12\12 0012.
 */

public class MyCalendarView extends View {
    private Paint titleDayPaint;
    private Paint titleLinePaint;
    private Paint monthPaint;
    private Paint circlePaint;
    private Context mContext;
    private int width;
    private int height;

    public MyCalendarView(Context context) {
        super(context);
    }

    public MyCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
        
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTitleDay(canvas);
        drawTitleLine(canvas);
        drawMonth(canvas);
        drawDay(canvas, 31);
    }

    private int downX = 0,downY = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventCode=  event.getAction();
        switch(eventCode){
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int) event.getX();
                int upY = (int) event.getY();
                if(Math.abs(upX-downX) < 10 && Math.abs(upY - downY) < 10){//点击事件
                    performClick();
                    //doClickAction((upX + downX)/2,(upY + downY)/2);
                }
                break;
        }
        return true;
    }

    private void init() {
        titleDayPaint = new Paint();
        titleDayPaint.setTextSize(DensityUtil.sp2px(mContext, 15));
        titleDayPaint.setColor(mContext.getResources().getColor(R.color.black));


        titleLinePaint = new Paint();
        titleLinePaint.setColor(mContext.getResources().getColor(R.color.color_f2f2f2));

        monthPaint = new Paint();
        monthPaint.setColor(mContext.getResources().getColor(R.color.color_21b2f7));
        monthPaint.setTextSize(DensityUtil.sp2px(mContext, 20));

        circlePaint = new Paint();
        circlePaint.setColor(mContext.getResources().getColor(R.color.color_00a6f5));
    }

    /**
     * 绘制顶部日期
     */
    private void drawTitleDay(Canvas canvas) {
        //初始X坐标（dp）
        int defaultX = 20;
        //初始Y坐标（dp）
        int defaultY = 20;
        //左右边距之和（dp）
        int sumMargin = 40;
        //获取单字的宽度
        float textWidth = titleDayPaint.measureText("六");
        //字与字之间的宽度
        int perWidth = (int) ((width - DensityUtil.dip2px(mContext, sumMargin) - textWidth) / 6);
        canvas.drawText("日", DensityUtil.dip2px(mContext, defaultX), DensityUtil.dip2px(mContext, defaultY), titleDayPaint);
        canvas.drawText("一", DensityUtil.dip2px(mContext, defaultX) + perWidth, DensityUtil.dip2px(mContext, defaultY), titleDayPaint);
        canvas.drawText("二", DensityUtil.dip2px(mContext, defaultX) + perWidth * 2, DensityUtil.dip2px(mContext, defaultY), titleDayPaint);
        canvas.drawText("三", DensityUtil.dip2px(mContext, defaultX) + perWidth * 3, DensityUtil.dip2px(mContext, defaultY), titleDayPaint);
        canvas.drawText("四", DensityUtil.dip2px(mContext, defaultX) + perWidth * 4, DensityUtil.dip2px(mContext, defaultY), titleDayPaint);
        canvas.drawText("五", DensityUtil.dip2px(mContext, defaultX) + perWidth * 5, DensityUtil.dip2px(mContext, defaultY), titleDayPaint);
        canvas.drawText("六", DensityUtil.dip2px(mContext, defaultX) + perWidth * 6, DensityUtil.dip2px(mContext, defaultY), titleDayPaint);
    }

    /**
     * 绘制顶部日期下面的横线
     *
     * @param canvas
     */
    private void drawTitleLine(Canvas canvas) {
        canvas.drawLine(0, DensityUtil.dip2px(mContext, 30), width, DensityUtil.dip2px(mContext, 30), titleLinePaint);
    }

    /**
     * 绘制月份
     */
    private void drawMonth(Canvas canvas) {
        canvas.drawText("2017年10月", DensityUtil.dip2px(mContext, 20), DensityUtil.dip2px(mContext, 80), monthPaint);
    }

    /**
     * 绘制日历
     *
     * @param canvas
     * @param day    当月天数
     */
    private void drawDay(Canvas canvas, int day) {
        //默认文本
        String defaultText = "20";
        //初始X坐标（dp）
        int defaultX = 20;
        //初始Y坐标（dp）
        int defaultY = 130;
        //左右边距之和（dp）
        int sumMargin = 40;
        //左右边距之和（dp）
        int defaultHeight = 30;
        //圆的半径（dp）
        int radius = 20;
        //获取单日的宽度
        float textWidth = titleDayPaint.measureText(defaultText);
        //获取文字的高度
        Rect rect = new Rect();
        titleDayPaint.getTextBounds(defaultText, 0, defaultText.length(), rect);
        int textHeight = rect.height();
        //日期之间的宽度
        int perWidth = (int) ((width - DensityUtil.dip2px(mContext, sumMargin) - textWidth) / 6);
        //日期之间的高度
        int perHeight = defaultHeight + textHeight;

        for (int i = 0; i < day; i++) {
            int x = DensityUtil.dip2px(mContext, defaultX) + perWidth * (i % 7);
            int y = DensityUtil.dip2px(mContext, defaultY + perHeight * (i / 7));
            float radiusX = x + textWidth / 2;
            float radiusY = y - textHeight / 2;

            //绘制字体
            canvas.drawText(String.valueOf(i+1),x,y,titleDayPaint);
            //绘制圆
            canvas.drawCircle(radiusX , radiusY , DensityUtil.dip2px(mContext,radius),circlePaint);
        }
    }
}
