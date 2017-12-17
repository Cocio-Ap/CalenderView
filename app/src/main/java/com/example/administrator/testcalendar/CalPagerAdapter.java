package com.example.administrator.testcalendar;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by nat.xue on 2017/12/16.
 *
 * @Description
 */

public class CalPagerAdapter extends PagerAdapter{
    private List<CalView> mListViews;

    public CalPagerAdapter(List<CalView> mListViews) {
        this.mListViews = mListViews;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)   {
        if(mListViews == null || mListViews.size() == 0) return;
        container.removeView(mListViews.get(position));//删除页卡
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(mListViews == null) return new Object();
        container.addView(mListViews.get(position), 0);//添加页卡
        return mListViews.get(position);
    }

    @Override
    public int getCount() {
        if(mListViews == null) return 0;
        return  mListViews.size();//返回页卡的数量
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0==arg1;
    }
}
