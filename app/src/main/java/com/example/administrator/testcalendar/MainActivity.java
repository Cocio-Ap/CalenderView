package com.example.administrator.testcalendar;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    //选中点的个数
    private final int DEFAULT_CLICK_CALENDER_NUM = 2;
    private final int CALENDER_VIEW_NUM = 100;
    private Subscription rxSubscription;
    //选中的日期表
    //表内数据是按升序排列的
    private List<CalendarData> mCalList;
    //传递List<CalendarData>数据
    private CalendarListData calendarListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewpager = findViewById(R.id.viewpager);
        mCalList = new ArrayList<>(DEFAULT_CLICK_CALENDER_NUM);
        calendarListData = new CalendarListData();
        int year = DateUtils.getYear();
        int month = DateUtils.getMonth();

        List<CalView> mListViews = getCalViewList(year, month,CALENDER_VIEW_NUM);
        CalPagerAdapter adapter = new CalPagerAdapter(mListViews);
        viewpager.setAdapter(adapter);
        //将mListViews.size的中心设为初始显示界面
        viewpager.setCurrentItem((mListViews.size()-1)/2);


        //监听不同CalenderView中传递过来的点击日期
        rxSubscription = RxBus.getInstance().toObserverable(CalendarData.class)
                .subscribe(new Action1<CalendarData>() {
                    @Override
                    public void call(CalendarData data) {
                        onReceiveClickData(data);
                        calendarListData.mClickCalList = mCalList;
                        RxBus.getInstance().post(calendarListData);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (!rxSubscription.isUnsubscribed()){
            rxSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    /**
     * 接收到CalendarData 数据后的处理
     * @param data
     */
    private void onReceiveClickData(CalendarData data){
        if(data == null) return;
        //使mCalList为升序
        if (mCalList.size() == 0) {
            mCalList.add(data);
        } else if (mCalList.size() == 1) {
            switch (CalendarUtil.compareClaDate(data,mCalList.get(0))){
                case CalendarUtil.DATA_LESS:  //小于
                    mCalList.set(0, data);
                    break;
                case CalendarUtil.DATA_EQUAL:  //相等
                    break;
                case CalendarUtil.DATA_MORE:  //大于
                    mCalList.add(data);
                    break;
            }
        } else if (mCalList.size() == 2) {
            if (CalendarUtil.compareClaDate(data,mCalList.get(0)) == 1 || CalendarUtil.compareClaDate(data,mCalList.get(1)) == 1) return;
            mCalList.clear();
            mCalList.add(data);
        }
    }

    /**
     * 获取CalenderView List
     * 返回的数据是以year，month为中心，前后各加num个View的集合
     * @param year 年
     * @param month 月
     * @param num CalenderView个数
     * @return List<CalView>
     */
    private List<CalView> getCalViewList(int year,int month,int num){
        List<CalView> mListViews = new ArrayList<>();
        CalendarData calData;
        CalView calendarView;
        //以year，month为中心，前后各加num个View的集合
        for(int i = -num;i <= num;i++ ){
            calData = CalendarUtil.newInstance(year,month + i);
            calendarView = new CalView(this,calData,mCalList);
            mListViews.add(calendarView);
        }
        return mListViews;
    }
}
