package com.example.administrator.testcalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017\12\12
 */

public class MyCalendarView extends View {
    private Paint textBlackPaint;
    private Paint textWhitePaint;
    private Paint titleDayPaint;
    private Paint titleLinePaint;
    private Paint monthPaint;
    private Paint circlePaint;
    private Paint dayLinePaint;
    private Context mContext;
    //屏幕宽度
    private int width;
    //日期与中心店映射表
    private HashMap<Integer, DayPoint> dayMap;
    //圆的半径（dp）
    private int radius = 20;
    //天数
    private int sumDay;
    //选中日期列表
    private List<Integer> clickDayList;

    public MyCalendarView(Context context) {
        super(context);
    }

    public MyCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initPaint();
        initData();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTitleDay(canvas);
        drawTitleLine(canvas);
        drawMonth(canvas);
        drawDay2DayLine(canvas);
        drawDayCircle(canvas);
        drawDay(canvas, 31);

    }

    private int downX = 0, downY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventCode = event.getAction();
        switch (eventCode) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int) event.getX();
                int upY = (int) event.getY();
                if (Math.abs(upX - downX) < 10 && Math.abs(upY - downY) < 10) {
                    int day = getTouchDay(downX, downY);
                    onClickDay(day);
                    invalidate();
                }
                break;
        }
        return true;
    }

    private void initPaint() {
        titleDayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titleDayPaint.setTextSize(DensityUtil.sp2px(mContext, 15));
        titleDayPaint.setColor(mContext.getResources().getColor(R.color.black));

        titleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titleLinePaint.setColor(mContext.getResources().getColor(R.color.color_f2f2f2));

        monthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        monthPaint.setColor(mContext.getResources().getColor(R.color.color_21b2f7));
        monthPaint.setTextSize(DensityUtil.sp2px(mContext, 20));

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(mContext.getResources().getColor(R.color.color_00a6f5));

        textBlackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textBlackPaint.setTextSize(DensityUtil.sp2px(mContext, 15));
        textBlackPaint.setColor(mContext.getResources().getColor(R.color.black));

        textWhitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textWhitePaint.setTextSize(DensityUtil.sp2px(mContext, 15));
        textWhitePaint.setColor(mContext.getResources().getColor(R.color.white));

        dayLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dayLinePaint.setTextSize(DensityUtil.sp2px(mContext, 15));
        dayLinePaint.setColor(mContext.getResources().getColor(R.color.color_d9f2fe));
    }

    private void initData() {
        dayMap = new HashMap<>();
        clickDayList = new ArrayList<>();
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
        //行间距（dp）
        int defaultHeight = 30;
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
        //日期是否被选中
        boolean isDayChoosen = false;
        Paint paint;
        String text = null;
        this.sumDay = day;

        for (int i = 0; i < day; i++) {
            int x = DensityUtil.dip2px(mContext, defaultX) + perWidth * (i % 7);
            int y = DensityUtil.dip2px(mContext, defaultY + perHeight * (i / 7));
            float radiusX = x + textWidth / 2;
            float radiusY = y - textHeight / 2;

            //绘制字体
            if (clickDayList.size() != 0) {
                for (Integer mDay : clickDayList) {
                    if (mDay == i + 1) {
                        isDayChoosen = true;
                        break;
                    }
                }
            }
            paint = isDayChoosen ? textWhitePaint : textBlackPaint;
            text = i + 1 < 10 ? " " + String.valueOf(i + 1) : String.valueOf(i + 1);
            canvas.drawText(text, x, y, paint);
            isDayChoosen = false;
            //将日期圆的半径存起来
            DayPoint dayPoint = new DayPoint(radiusX, radiusY);
            dayMap.put(i + 1, dayPoint);
        }
    }

    /**
     * 绘制选中圆
     *
     * @param canvas
     */
    private void drawDayCircle(Canvas canvas) {
        if (dayMap == null || clickDayList == null || clickDayList.size() == 0) return;
        for (int i = 0; i < clickDayList.size(); i++) {
            int day = clickDayList.get(i);
            DayPoint point = dayMap.get(day);
            canvas.drawCircle(point.x, point.y, DensityUtil.dip2px(mContext, radius), circlePaint);
        }
    }

    /**
     * 绘制两个不同日期之间的连线
     *
     * @param canvas
     */
    private void drawDay2DayLine(Canvas canvas) {
        if (dayMap == null || clickDayList == null || clickDayList.size() != 2) return;
        int firstDay = clickDayList.get(0);
        int lastDay = clickDayList.get(1);
        int startRow = ((firstDay - 1) / 7) + 1;
        int endRow = ((lastDay - 1) / 7) + 1;
        Rect rect;
        DayPoint startPoint;
        DayPoint endPoint;

        for (int i = startRow; i <= endRow; i++) {
            if (i == startRow) { //连线的第一行
                startPoint = dayMap.get(firstDay);
                if (i == endRow) { //连线只有一行
                    endPoint = dayMap.get(lastDay);
                } else { //连线不止一行
                    endPoint = dayMap.get(i * 7);
                    canvas.drawCircle(endPoint.x, endPoint.y, DensityUtil.dip2px(mContext, radius), dayLinePaint);
                }
                rect = new Rect((int) startPoint.x,
                        (int) startPoint.y - DensityUtil.dip2px(mContext, radius),
                        (int) endPoint.x,
                        (int) endPoint.y + DensityUtil.dip2px(mContext, radius));
            } else { //连线的其他行
                startPoint = dayMap.get((i - 1) * 7 + 1);
                if (i == endRow) { //连线的最后一行
                    endPoint = dayMap.get(lastDay);
                } else { //非最后一行
                    endPoint = dayMap.get(i * 7);
                    canvas.drawCircle(endPoint.x, endPoint.y, DensityUtil.dip2px(mContext, radius), dayLinePaint);
                }
                canvas.drawCircle(startPoint.x, startPoint.y, DensityUtil.dip2px(mContext, radius), dayLinePaint);
                rect = new Rect((int) startPoint.x,
                        (int) startPoint.y - DensityUtil.dip2px(mContext, radius),
                        (int) endPoint.x,
                        (int) endPoint.y + DensityUtil.dip2px(mContext, radius));
            }
            canvas.drawRect(rect, dayLinePaint);
        }
    }

    /**
     * 获取半弧范围
     *
     * @param dayPoint
     * @return
     */
    private RectF getArcRectF(DayPoint dayPoint) {
        if (dayPoint == null) return null;
        RectF rectF = new RectF(dayPoint.x - DensityUtil.dip2px(mContext, radius),
                dayPoint.y - DensityUtil.dip2px(mContext, radius),
                dayPoint.x + DensityUtil.dip2px(mContext, radius),
                dayPoint.y + DensityUtil.dip2px(mContext, radius));
        return rectF;
    }

    /**
     * 获取某日圆的中心点
     *
     * @param day
     * @return
     */
    private DayPoint getPoint(int day) {
        if (dayMap == null) return null;
        return dayMap.get(day);
    }

    /**
     * 获取触碰点的日期
     *
     * @return 0：并没有点击到某日区域/ Other：具体的日期
     */
    private int getTouchDay(int x, int y) {
        int mDay = 0;
        DayPoint firstPoint = getPoint(1);
        DayPoint lastPoint = getPoint(sumDay);
        Rect rect = new Rect(0,
                (int) firstPoint.y - DensityUtil.dip2px(mContext, radius),
                width,
                (int) lastPoint.y + DensityUtil.dip2px(mContext, radius));
        //判断是否在日历区域
        if (!rect.contains(x, y)) {
            return mDay;
        }

        //遍历每天的中心圆区域
        Iterator it = dayMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            DayPoint point = (DayPoint) entry.getValue();
            Rect rect1 = new Rect((int) point.x - DensityUtil.dip2px(mContext, radius),
                    (int) point.y - DensityUtil.dip2px(mContext, radius),
                    (int) point.x + DensityUtil.dip2px(mContext, radius),
                    (int) point.y + DensityUtil.dip2px(mContext, radius));
            if (rect1.contains(x, y)) {
                mDay = (int) entry.getKey();
                break;
            }
        }
        return mDay;
    }

    /**
     * 点击事件逻辑
     *
     * @param day
     */
    private void onClickDay(int day) {
        if (day <= 0 || day > sumDay) return;

        if (clickDayList.size() == 0) {
            clickDayList.add(day);
        } else if (clickDayList.size() == 1) {
            if (clickDayList.get(0) != day) {
                if (clickDayList.get(0) > day) {
                    //点击的日期小于储存的日期
                    //覆盖
                    clickDayList.set(0, day);
                } else {
                    //点击的日期大于储存的日期
                    //添加
                    clickDayList.add(day);
                }
            }
        } else if (clickDayList.size() == 2) {
            if (clickDayList.get(0) == day || clickDayList.get(1) == day) return;
            clickDayList.clear();
            clickDayList.add(day);
        }
    }


    //天
    //中心点坐标
    class DayPoint {
        public float x;
        public float y;

        DayPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

}
