package com.example.administrator.testcalendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017\12\13 0013.
 */

public class CalendarFragment extends Fragment {
    private MyCalendarView calendarView;
    private Bundle bundle;
    //选中的日期列表
    //表内数据是按升序排列的
    private List<CalendarData> mCalList;
    private CalendarData data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarView = view.findViewById(R.id.calView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bundle = getArguments();
        if(bundle != null){
            data = (CalendarData) bundle.getSerializable("cal");
            mCalList = (List<CalendarData>) bundle.getSerializable("chooseCal");
        }
        calendarView.init(data,mCalList);
    }


    public static CalendarFragment newInstance(CalendarData data,List<CalendarData> mCalList){
        CalendarFragment calendarFragment = new CalendarFragment();
        if(data == null) return calendarFragment;
        Bundle bundle = new Bundle();
        bundle.putSerializable("cal",data);
        bundle.putSerializable("chooseCal", (Serializable) mCalList);
        calendarFragment.setArguments(bundle);
        return calendarFragment;
    }
}
