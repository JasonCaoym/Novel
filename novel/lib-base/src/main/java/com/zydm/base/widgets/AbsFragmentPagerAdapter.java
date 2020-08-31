package com.zydm.base.widgets;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import com.zydm.base.ui.fragment.BaseFragment;

/**
 * Created by yin on 2016/6/29.
 */
public abstract class AbsFragmentPagerAdapter extends FragmentPagerAdapter {

    private BaseFragment mCurFragment;
    protected int mCount;
    private int mCurPosition;

    public AbsFragmentPagerAdapter(FragmentManager fragmentManager, int count) {
        super(fragmentManager);
        this.mCount = count;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCurFragment = (BaseFragment) object;
        mCurPosition = position;
    }

    public BaseFragment getCurFragment() {
        return mCurFragment;
    }

    public int getCurPosition() {
        return mCurPosition;
    }
}
