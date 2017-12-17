package com.example.administrator.testcalendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nat.xue on 2017\12\15
 */

public class CalendarListData {
    private final int DEFAULT_CLICK_CALENDER_NUM = 2;
    //选中日期的集合
    public List<CalendarData> mClickCalList;

    public CalendarListData(){
        mClickCalList = new ArrayList<>(DEFAULT_CLICK_CALENDER_NUM);
    }
}
