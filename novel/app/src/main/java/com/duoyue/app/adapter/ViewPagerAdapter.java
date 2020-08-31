package com.duoyue.app.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ViewPagerAdapter<T extends Fragment> extends FragmentStatePagerAdapter {

    private List<T> mFragmentList = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private T mCurrentFragment;


    public ViewPagerAdapter(FragmentManager fm, List<T> mList, List<String> titles) {
        super(fm);
        setDat(mList, titles);
    }

    public void setDat(List<T> mList, List<String> titles) {
        if (mList != null && titles != null) {
            this.mFragmentList.clear();
            this.mFragmentList.addAll(mList);
            this.mTitles.clear();
            this.mTitles.addAll(titles);
        }
    }


    public void add(T fragment) {
        if (isEmpty()) {
            mFragmentList = new ArrayList<>();
        }
        mFragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        if (isEmpty()) {
            return null;
        } else {
            if (position >= mFragmentList.size()) {
                position = mFragmentList.size() - 1;
            } else if (position < 0) {
                position = 0;
            }
        }
        return  mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return isEmpty() ? 0 : mFragmentList.size();
    }

    public boolean isEmpty() {
        return mFragmentList == null;

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null) {
            return mTitles.get(position);
        }
        return super.getPageTitle(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentFragment = (T) object;
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public T getCurrentFragment() {
        return mCurrentFragment;
    }

    public T getFragmentByIndex(int position) {
        if (mFragmentList != null && mFragmentList.size() > position) {
            return mFragmentList.get(position);
        }
        return null;
    }
}
