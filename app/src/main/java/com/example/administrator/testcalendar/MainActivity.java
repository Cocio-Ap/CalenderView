package com.example.administrator.testcalendar;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewpager;
    private CalPagerAdapter adapter;
    private CalendarData preCalData,currentCalData,nextCalData;
    private int year;
    private int month;
    private Subscription rxSubscription;
    //选中的日期表
    //表内数据是按升序排列的
    private List<CalendarData> mCalList;
    //传递List<CalendarData>数据
    private CalendarListData calendarListData;
    private List<CalView> mListViews ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewpager = findViewById(R.id.viewpager);
        mCalList = new ArrayList<>();
        calendarListData = new CalendarListData();
        year = DateUtils.getYear();
        month = DateUtils.getMonth();

        preCalData = CalendarUtil.newInstance(year,month-1);
        currentCalData = CalendarUtil.newInstance(year,month);
        nextCalData = CalendarUtil.newInstance(year,month+1);


        CalView calendarView1 = new CalView(this,preCalData,mCalList);
        CalView calendarView2 = new CalView(this,currentCalData,mCalList);
        CalView calendarView3 = new CalView(this,nextCalData,mCalList);
        mListViews = new ArrayList<>();
        mListViews.add(calendarView1);
        mListViews.add(calendarView2);
        mListViews.add(calendarView3);
        adapter = new CalPagerAdapter(mListViews);
        viewpager.setAdapter(adapter);
        viewpager.setOnPageChangeListener(pageChangeListener);
        viewpager.setCurrentItem(1);


        //监听不同Fragment中传递过来的点击日期
        rxSubscription = RxBus.getInstance().toObserverable(CalendarData.class)
                .subscribe(new Action1<CalendarData>() {
                    @Override
                    public void call(CalendarData data) {
                        onReceiveClickData(data);
                        calendarListData.mCalData = mCalList;
                        RxBus.getInstance().post(calendarListData);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!rxSubscription.isUnsubscribed()){
            rxSubscription.unsubscribe();
        }
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

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

//    private List<CalView> getCalViewList(int currentYear,int currentMonth,int num){
//        List<CalView> mListViews = new ArrayList<>();
//        CalendarData currentCalData = CalendarUtil.newInstance(year,month);
//        for(int i )
//    }
}
