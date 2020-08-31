package com.duoyue.app.ui.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.duoyue.app.adapter.ViewPagerAdapter;
import com.duoyue.app.ui.view.PagerTitleIndexView;
import com.duoyue.app.ui.widget.ExpandView;
import com.duoyue.app.ui.widget.HXLinePagerIndicator;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.zydm.base.ui.fragment.BaseFragment;
import com.zydm.base.utils.ViewUtils;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 发现Tab页面
 */
public class BookFindFragment extends BaseFragment implements View.OnClickListener
{
    /**
     * 今天推荐Tab位置
     */
    private static final int RECOMM_TAB_INDEX = 0;

    /**
     * 标题列表
     */
    private List<String> mTitles = Arrays.asList("推荐小说", "附近的人");

    /**
     * ViewPager
     */
    private ViewPager mViewPager;

    /**
     * ViewPagerAdapter
     */
    private ViewPagerAdapter mPagerAdapter;

    /**
     * Fragment列表
     */
    private List<Fragment> mFragments;

    /**
     * 推荐Fragment
     */
    private BookFindRecomFragment mRecomFragment;

    /**
     * 展开组件.
     */
    private ExpandView mExpandView;

    /**
     * 频道选择
     */
    private TextView mFrequencyTextView;

    /**
     * 全部频道名称
     */
    private TextView mAllFrequencyNameTextView;

    /**
     * 全部频道选中状态
     */
    private ImageView mAllFrequencyStatusImageView;

    /**
     * 男频名称
     */
    private TextView mMaleFrequencyNameTextView;

    /**
     * 男频选中状态
     */
    private ImageView mMaleFrequencyStatusImageView;

    /**
     * 女频名称
     */
    private TextView mFemaleFrequencyNameTextView;

    /**
     * 女频选中状态
     */
    private ImageView mFemaleFrequencyStatusImageView;

    @Override
    public void onCreateView(@Nullable Bundle savedInstanceState)
    {
        setContentView(R.layout.book_find_fragment);
        //获取ViewPager对象.
        mViewPager = findView(R.id.book_find_viewpager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1)
            {
                if (i1 > 1 && mFrequencyTextView != null && mFrequencyTextView.getVisibility() == View.VISIBLE)
                {
                    //隐藏频道选择.
                    mFrequencyTextView.setVisibility(View.GONE);
                    collapse();
                }
            }
            @Override
            public void onPageSelected(int i)
            {
            }
            @Override
            public void onPageScrollStateChanged(int i)
            {
                //判断是否显示频道选择.
                if (mViewPager != null && mViewPager.getCurrentItem() == 0 && mFrequencyTextView != null && mFrequencyTextView.getVisibility() != View.VISIBLE)
                {
                    mFrequencyTextView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //if (FirstModeUtil.getInstance(getActivity()).getMode() == null)
        //{
        //初始化展开组件.
        initExpandView();
        setupViewPager();
        initMagicIndex();
            //NotificationsUtils.isFirstIn(getContext(), getFragmentManager(), PageNameConstants.BOOK_CITY);
        //}
    }

    /**
     * 初始化展开组件.
     */
    private void initExpandView()
    {
        //加载频道选择View.
        View frequencyListView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_find_recomm_frequency_list, null);
        //所有频道.
        frequencyListView.findViewById(R.id.all_frequency_layout).setOnClickListener(this);
        //全部频道名称
        mAllFrequencyNameTextView = frequencyListView.findViewById(R.id.all_frequency_name_textview);
        //全部频道选中状态
        mAllFrequencyStatusImageView = frequencyListView.findViewById(R.id.all_frequency_status_imageview);
        //男频.
        frequencyListView.findViewById(R.id.male_frequency_layout).setOnClickListener(this);
        //男频名称
        mMaleFrequencyNameTextView = frequencyListView.findViewById(R.id.male_frequency_name_textview);
        //男频选中状态
        mMaleFrequencyStatusImageView = frequencyListView.findViewById(R.id.male_frequency_status_imageview);
        //女频.
        frequencyListView.findViewById(R.id.female_frequency_layout).setOnClickListener(this);
        //女频名称
        mFemaleFrequencyNameTextView = frequencyListView.findViewById(R.id.female_frequency_name_textview);
        //女频选中状态
        mFemaleFrequencyStatusImageView = frequencyListView.findViewById(R.id.female_frequency_status_imageview);
        //获取收起/展开View.
        mExpandView = findView(R.id.recomm_expandview);
        mExpandView.addExpandView(frequencyListView);
        //频道选择.
        mFrequencyTextView = findView(R.id.frequency_textview);
        //设置点击事件.
        mFrequencyTextView.setClickable(true);
        mFrequencyTextView.setOnClickListener(this);
    }

    private void initMagicIndex()
    {
        MagicIndicator magicIndicator = findView(R.id.book_find_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdapter(new CommonNavigatorAdapter()
        {
            @Override
            public int getCount()
            {
                return mTitles == null ? 0 : mTitles.size();
            }
            @Override
            public IPagerTitleView getTitleView(Context context, final int index)
            {
                final SimplePagerTitleView simplePagerTitleView = new PagerTitleIndexView(context);
                simplePagerTitleView.setPadding(ViewUtils.dp2px(25), 0, ViewUtils.dp2px(25), 0);
                simplePagerTitleView.setText(mTitles.get(index));
                simplePagerTitleView.setTextSize(17);
                //simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.text_black_333));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.standard_red_main_color_c1));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (mViewPager.getCurrentItem() == index)
                        {
                            return;
                        }
                        mViewPager.setCurrentItem(index);
                        Glide.get(simplePagerTitleView.getContext()).clearMemory();
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context)
            {
                HXLinePagerIndicator indicator = new HXLinePagerIndicator(context);
                indicator.setRoundRadius(ViewUtils.dp2px(10f));
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineWidth(UIUtil.dip2px(context, 20));
                indicator.setColors(getResources().getColor(R.color.standard_red_main_color_c1));
                Glide.get(context).clearMemory();
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    private void setupViewPager()
    {
        if (mFragments != null)
        {
            mViewPager.removeAllViewsInLayout();
            mFragments.clear();
        }
        mFragments = new ArrayList<>();
        Fragment fragment = null;
        for (int index = 0; index < mTitles.size(); index++)
        {
            Bundle bundle = new Bundle();
            if (index == RECOMM_TAB_INDEX)
            {
                //推荐小说.
                mRecomFragment = new BookFindRecomFragment();
                bundle.putInt("type", RECOMM_TAB_INDEX);
                mRecomFragment.setArguments(bundle);
                mFragments.add(mRecomFragment);
            } else
            {
                //附件的人.
                fragment = new BookNewListFragment();
                bundle.putInt("type", index);
                fragment.setArguments(bundle);
                mFragments.add(fragment);
            }
        }
        //为ViewPager设置适配器
        if (mPagerAdapter == null)
        {
            mPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mFragments, mTitles);
        } else {
            mPagerAdapter.setDat(mFragments, mTitles);
        }
        mViewPager.setOffscreenPageLimit(mTitles.size());
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onClick(@NotNull View view)
    {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.frequency_textview:
                //收起/展开.
                if (mExpandView.isExpand())
                {
                    //收起.
                    collapse();
                } else
                {
                    //展开.
                   expand();
                }
                break;
            case R.id.all_frequency_layout:
                //全部频道.
                mAllFrequencyNameTextView.setTextColor(getContext().getResources().getColor(R.color.standard_red_main_color_c1));
                mAllFrequencyStatusImageView.setVisibility(View.VISIBLE);
                mFrequencyTextView.setText(mAllFrequencyNameTextView.getText());
                //男频.
                mMaleFrequencyNameTextView.setTextColor(getContext().getResources().getColor(R.color.color_898989));
                mMaleFrequencyStatusImageView.setVisibility(View.INVISIBLE);
                //女频.
                mFemaleFrequencyNameTextView.setTextColor(getContext().getResources().getColor(R.color.color_898989));
                mFemaleFrequencyStatusImageView.setVisibility(View.INVISIBLE);
                //收起.
                collapse();
                //切换频道.
                if (mRecomFragment != null)
                {
                    mRecomFragment.switchFrequency(0);
                }
                break;
            case R.id.male_frequency_layout:
                //男频.
                mMaleFrequencyNameTextView.setTextColor(getContext().getResources().getColor(R.color.standard_red_main_color_c1));
                mMaleFrequencyStatusImageView.setVisibility(View.VISIBLE);
                mFrequencyTextView.setText(mMaleFrequencyNameTextView.getText());
                //全部频道.
                mAllFrequencyNameTextView.setTextColor(getContext().getResources().getColor(R.color.color_898989));
                mAllFrequencyStatusImageView.setVisibility(View.INVISIBLE);
                //女频.
                mFemaleFrequencyNameTextView.setTextColor(getContext().getResources().getColor(R.color.color_898989));
                mFemaleFrequencyStatusImageView.setVisibility(View.INVISIBLE);
                //收起.
                collapse();
                //切换频道.
                if (mRecomFragment != null)
                {
                    mRecomFragment.switchFrequency(1);
                }
                break;
            case R.id.female_frequency_layout:
                //女频.
                mFemaleFrequencyNameTextView.setTextColor(getContext().getResources().getColor(R.color.standard_red_main_color_c1));
                mFemaleFrequencyStatusImageView.setVisibility(View.VISIBLE);
                mFrequencyTextView.setText(mFemaleFrequencyNameTextView.getText());
                //全部频道.
                mAllFrequencyNameTextView.setTextColor(getContext().getResources().getColor(R.color.color_898989));
                mAllFrequencyStatusImageView.setVisibility(View.INVISIBLE);
                //男频.
                mMaleFrequencyNameTextView.setTextColor(getContext().getResources().getColor(R.color.color_898989));
                mMaleFrequencyStatusImageView.setVisibility(View.INVISIBLE);
                //收起.
                collapse();
                //切换频道.
                if (mRecomFragment != null)
                {
                    mRecomFragment.switchFrequency(2);
                }
                break;
        }
    }

    /**
     * 收起
     */
    private void collapse()
    {
        //收起.
        if (mExpandView != null && mExpandView.isExpand())
        {
            mExpandView.onCollapse();
            Drawable arrowDonw = getResources().getDrawable(R.mipmap.arrow_donw);
            arrowDonw.setBounds(0, 0, arrowDonw.getMinimumWidth(), arrowDonw.getMinimumHeight());
            mFrequencyTextView.setCompoundDrawables(null, null, arrowDonw, null);
        }
    }

    /**
     * 展开
     */
    private void expand()
    {
        if (mExpandView != null)
        {
            mExpandView.onExpand();
            Drawable arrowDonw = getResources().getDrawable(R.mipmap.arrow_up);
            arrowDonw.setBounds(0, 0, arrowDonw.getMinimumWidth(), arrowDonw.getMinimumHeight());
            mFrequencyTextView.setCompoundDrawables(null, null, arrowDonw, null);
        }
    }

    /**
     * 获取当前Tab名称
     *
     * @return
     */
    @Override
    public String getPageName() {
        return ViewUtils.getString(R.string.find);
    }

    /**
     * 登录成功.
     */
    public void onLoginSucc() {
        //重新更新数据
        setupViewPager();
        initMagicIndex();
    }

    /**
     *
     */
    public void setIsRead()
    {

    }

    public void resumeByHomePressed()
    {
        FuncPageStatsApi.intoDiscover(2);
        Logger.e("app#", "发现--从后台唤起");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //离开当前Activity时, 直接收起.
        collapse();
    }
}
