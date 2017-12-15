package com.example.administrator.testcalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017\12\12
 */

public class MyCalendarView extends View {
    private Paint textBlackPaint;
    private Paint textWhitePaint;
    private Paint textCurrentDayPaint;
    private Paint titleDayPaint;
    private Paint titleLinePaint;
    private Paint monthPaint;
    private Paint circlePaint;
    private Paint dayLinePaint;
    private Paint currentDayCirclePaint;
    private Context mContext;
    //屏幕宽度
    private int width;
    //日期与中心店映射表
    private HashMap<Integer, DayPoint> dayMap;
    //圆的半径（dp）
    private int radius = 20;
    //天数
    private int sumDay;
    //当天
    //默认为0,表示不是赋值的日期不是实时的日期/非0表示实时的日期
    private int currentDay;
    //选中的日期列表
    //表内数据是按升序排列的
    private List<CalendarData> clickCalList;
    //初始化日期
    private CalendarData defaultData;
    private Subscription rxSubscription;


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
        if(defaultData == null) return;

        drawTitleDay(canvas);
        drawTitleLine(canvas);
        drawMonth(canvas);
        drawDay2DayLine(canvas);
        drawClickDayCircle(canvas);
        drawDay(canvas);
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!rxSubscription.isUnsubscribed()){
            rxSubscription.unsubscribe();
        }
    }

    /**
     *
     * @param defaultData 赋值日期
     * @param clickCalList 选中的日期列表
     *                     表内数据需按升序排列
     */
    public void init(CalendarData defaultData,List<CalendarData> clickCalList){
        if(defaultData == null || clickCalList == null) return;
        this.defaultData = defaultData;
        this.sumDay = DateUtils.getDays(defaultData.year,defaultData.month);
        this.currentDay = getCurrentDay(defaultData.year,defaultData.month);
        this.clickCalList = clickCalList;
        invalidate();
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

        textCurrentDayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textCurrentDayPaint.setTextSize(DensityUtil.sp2px(mContext, 15));
        textCurrentDayPaint.setColor(mContext.getResources().getColor(R.color.color_00a6f5));

        dayLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dayLinePaint.setTextSize(DensityUtil.sp2px(mContext, 15));
        dayLinePaint.setColor(mContext.getResources().getColor(R.color.color_d9f2fe));

        currentDayCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentDayCirclePaint.setColor(mContext.getResources().getColor(R.color.color_bfe9fc));
    }

    private void initData() {
        dayMap = new HashMap<>();
        //监听传来的选中点集合
        rxSubscription = RxBus.getInstance().toObserverable(CalendarListData.class)
                .map(new Func1<CalendarListData, List<CalendarData>>() {
                    @Override
                    public List<CalendarData> call(CalendarListData calendarListData) {
                        return calendarListData.mCalData;
                    }
                })
                .subscribe(new Action1<List<CalendarData>>() {

                    @Override
                    public void call(List<CalendarData> data){
                        clickCalList = data;
                        invalidate();
                    }
                });
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
        String text = String.valueOf(defaultData.year) + "年" + String.valueOf(defaultData.month) + "月";
        canvas.drawText(text, DensityUtil.dip2px(mContext, 20), DensityUtil.dip2px(mContext, 80), monthPaint);
    }

    /**
     * 绘制日历
     *
     * @param canvas
     */
    private void drawDay(Canvas canvas) {
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
        boolean isDayChoose = false;
        Paint paint;
        String text;

        for (int i = 0; i < sumDay; i++) {
            int x = DensityUtil.dip2px(mContext, defaultX) + perWidth * (i % 7);
            int y = DensityUtil.dip2px(mContext, defaultY + perHeight * (i / 7));
            float radiusX = x + textWidth / 2;
            float radiusY = y - textHeight / 2;

            //将日期圆的半径存起来
            DayPoint dayPoint = new DayPoint(radiusX, radiusY);
            dayMap.put(i + 1, dayPoint);

            //currentDay默认为0,表示不是赋值的日期不是实时的日期/非0表示实时的日期
            //如果是当天，还需要绘制当天的圆
            if(currentDay == i+1){
                boolean isCurrentDayChoose = false;
                //判断当天是否被选中
                if(clickCalList != null && clickCalList.size() != 0){
                    for (int j = 0; j < clickCalList.size(); j++) {
                        CalendarData data = clickCalList.get(j);
                        if(data == null || CalendarUtil.compareClaMonth(data,defaultData) != CalendarUtil.DATA_EQUAL) continue;
                        int day = data.day;
                        if(day == currentDay){
                            isCurrentDayChoose = true;
                            break;
                        }
                    }
                }
                //当天不被选中绘制圆
                if(!isCurrentDayChoose) canvas.drawCircle(dayPoint.x, dayPoint.y, DensityUtil.dip2px(mContext, radius), currentDayCirclePaint);
            }

            //绘制字体
            if (clickCalList.size() != 0) {
                for (CalendarData data : clickCalList) {
                    if(data == null || CalendarUtil.compareClaMonth(data,defaultData) != CalendarUtil.DATA_EQUAL) continue;
                    if (data.day == i + 1) {
                        isDayChoose = true;
                        break;
                    }
                }
            }
            //选中字体为白色
            //没选中字体:如果是实时当天，则为蓝色；否则为黑色
            if(isDayChoose){
                paint = textWhitePaint;
            }else if(currentDay == i+1){
                paint = textCurrentDayPaint;
            }else{
                paint = textBlackPaint;
            }
            //单位数天数加个空格
            text = i + 1 < 10 ? " " + String.valueOf(i + 1) : String.valueOf(i + 1);
            canvas.drawText(text, x, y, paint);
            isDayChoose = false;
        }
    }

    /**
     * 绘制选中时的圆
     *
     * @param canvas
     */
    private void drawClickDayCircle(Canvas canvas) {
        if (dayMap.size() == 0 || clickCalList == null || clickCalList.size() == 0) return;
        //绘制选中圆
        for (int i = 0; i < clickCalList.size(); i++) {
            CalendarData data = clickCalList.get(i);
            if(data == null) return;
            if(CalendarUtil.compareClaMonth(data,defaultData) == CalendarUtil.DATA_EQUAL){
                int day = data.day;
                DayPoint point = dayMap.get(day);
                canvas.drawCircle(point.x, point.y, DensityUtil.dip2px(mContext, radius), circlePaint);
            }
        }
    }

    /**
     * 绘制两个不同日期之间的连线
     *
     * @param canvas
     */
    private void drawDay2DayLine(Canvas canvas) {
        if (dayMap.size() == 0 || clickCalList == null || clickCalList.size() != 2) return;
        CalendarData d1 = clickCalList.get(0);
        CalendarData d2 = clickCalList.get(1);
        if(d1 == null || d2 == null) return;

        int firstDay = 0;
        int lastDay = 0;
        if(CalendarUtil.compareClaMonth(d1,defaultData) == CalendarUtil.DATA_LESS){
            //d1的月份小于当前页面的月份
            switch (CalendarUtil.compareClaMonth(d2,defaultData)){
                case CalendarUtil.DATA_LESS:
                    //不绘制
                    break;
                case CalendarUtil.DATA_EQUAL:
                    firstDay = 1;
                    lastDay = d2.day;
                    break;
                case CalendarUtil.DATA_MORE:
                    firstDay = 1;
                    lastDay = sumDay;
                    break;
            }
        }else if(CalendarUtil.compareClaMonth(d1,defaultData) == CalendarUtil.DATA_EQUAL) {
            //d1的月份等于当前页面的月份
            switch (CalendarUtil.compareClaMonth(d2, defaultData)) {
                case CalendarUtil.DATA_EQUAL:
                    firstDay = d1.day;
                    lastDay = d2.day;
                    break;
                case CalendarUtil.DATA_MORE:
                    firstDay = d1.day;
                    lastDay = sumDay;
                    break;
            }
        }else if(CalendarUtil.compareClaMonth(d1,defaultData) == CalendarUtil.DATA_MORE) {
            //d1的月份大于当前页面的月份
            //不绘制
            }

        if(firstDay == 0 || lastDay == 0) return;

        int startRow = ((firstDay - 1) / 7) + 1;
        int endRow = ((lastDay - 1) / 7) + 1;
        Rect rect;
        DayPoint startPoint;
        DayPoint endPoint;

        for (int i = startRow; i <= endRow; i++) {
            if (i == startRow) { //连线的第一行
                startPoint = dayMap.get(firstDay);
                endPoint = (i== endRow) ? dayMap.get(lastDay) : dayMap.get(i * 7);
                rect = new Rect((int) startPoint.x,
                        (int) startPoint.y - DensityUtil.dip2px(mContext, radius),
                        (int) endPoint.x,
                        (int) endPoint.y + DensityUtil.dip2px(mContext, radius));
            } else { //连线的其他行
                startPoint = dayMap.get((i - 1) * 7 + 1);
                endPoint = (i== endRow) ? dayMap.get(lastDay) : dayMap.get(i * 7);
                rect = new Rect((int) startPoint.x,
                        (int) startPoint.y - DensityUtil.dip2px(mContext, radius),
                        (int) endPoint.x,
                        (int) endPoint.y + DensityUtil.dip2px(mContext, radius));
            }
            canvas.drawCircle(startPoint.x, startPoint.y, DensityUtil.dip2px(mContext, radius), dayLinePaint);
            canvas.drawCircle(endPoint.x, endPoint.y, DensityUtil.dip2px(mContext, radius), dayLinePaint);
            canvas.drawRect(rect, dayLinePaint);
        }
    }

    /**
     * 获取某日圆的中心点
     *
     * @param day
     * @return
     */
    private DayPoint getPoint(int day) {
        if (dayMap.size() == 0) return null;
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

        CalendarData calendarData = new CalendarData();
        calendarData.year = defaultData.year;
        calendarData.month = defaultData.month;
        calendarData.day = day;
        RxBus.getInstance().post(calendarData);
    }

    /**
     * 获取当天的天数
     * 实时，当天
     * @param year
     * @param month
     * @return
     */
    private int getCurrentDay(int year,int month){
        int currentDay = 0;
        int currentYear = DateUtils.getYear();
        int currentMonth = DateUtils.getMonth();
        if (currentYear == year && currentMonth == month) {
            currentDay = DateUtils.getDay();
        }
        return currentDay;
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
