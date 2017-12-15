package com.example.administrator.testcalendar;

/**
 * Created by nat.xue on 2017\12\15 0015.
 */

public class CalendarUtil {
    public static final int DATA_LESS = 0;
    public static final int DATA_EQUAL = 1;
    public static final int DATA_MORE = 2;

    /**
     * 比较日期
     * 返回数据： 0表示d1小于d2/1表示相等/2表示d1大于d2
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int compareClaDate(CalendarData d1, CalendarData d2) {
        int res = compareData(d1.year, d2.year);
        if (res == DATA_EQUAL) {
            res = compareData(d1.month, d2.month);
            if (res == DATA_EQUAL) {
                res = compareData(d1.day, d2.day);
            }
        }
        return res;
    }

    /**
     * 比较月份
     * 返回数据： 0表示d1小于d2/1表示相等/2表示d1大于d2
     * @param d1
     * @param d2
     * @return
     */
    public static int compareClaMonth(CalendarData d1, CalendarData d2){
        int res = compareData(d1.year, d2.year);
        if (res == DATA_EQUAL) {
            res = compareData(d1.month, d2.month);
        }
        return res;
    }

    /**
     * 初始化
     * @param year 年
     * @param month 月
     * @return
     */
    public static CalendarData newInstance(int year,int month){
        CalendarData calendarData = new CalendarData();
        if(month < 0 || month > 13 || year < 0) return calendarData;
        if(month == 0){
            month = 12;
            year -= 1;
        }else if(month == 13){
            month = 1;
            year += 1;
        }
        calendarData.month = month;
        calendarData.year = year;
        return calendarData;
    }

    /**
     * 返回数据： 0表示d1小于d2/1表示相等/2表示d1大于d2
     *
     * @param d1
     * @param d2
     * @return
     */
    private static int compareData(int d1,int d2){
        int res;
        if(d1 > d2){
            res = DATA_MORE;
        }else if(d1 < d2){
            res = DATA_LESS;
        }else{
            res = DATA_EQUAL;
        }
        return res;
    }
}
