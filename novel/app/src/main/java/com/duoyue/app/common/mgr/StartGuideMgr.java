package com.duoyue.app.common.mgr;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.duoyue.app.bean.CategoryGroupBean;
import com.duoyue.app.common.data.request.bookrecord.ReadingTasteReq;
import com.duoyue.app.common.data.response.bookrecord.ReadingTasteResp;
import com.duoyue.app.event.ReadingTasteEvent;
import com.duoyue.app.presenter.CategoryPresenter;
import com.duoyue.app.ui.adapter.GuideCategoryAdapter;
import com.duoyue.app.upgrade.UpgradeManager;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.cache.RamCache;
import com.duoyue.lib.base.cache.StringParser;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.ui.read.ReadActivity;
import com.duoyue.mod.ad.api.ToutiaoAdReport;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.common.ParamKey;
import com.zydm.base.data.bean.CategoryBean;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.tools.PhoneStatusManager;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import com.zzdm.ad.router.BaseData;

import com.zzdm.ad.router.RouterPath;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 启动引导管理类
 *
 * @author caoym
 * @data 2019/4/1  20:55
 */
public class StartGuideMgr {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#StartGuideMgr";

    /**
     * JSON Key 性别
     */
    public static final String JSON_KEY_SEX = "SEX";

    /**
     * JSON Key 分类Id
     */
    public static final String JSON_KEY_CATEGORY = "CATEGORY";

    /**
     * SON Key 上报时间.
     */
    private static final String JSON_KEY_UPLOAD_TIME = "UPLOAD_TIME";

    /**
     * 性别-1:男生
     */
    public static final int SEX_MAN = 1;

    /**
     * 性别-1:女生
     */
    public static final int SEX_WOMAN = 2;
    /**
     * 性别-1:图书
     */
    public static final int BOOK = 3;

    /**
     * 当前类对象.
     */
    private static StartGuideMgr sInstance;
    private static String currPageId;

    /**
     * 引导页View.
     */
    private View mGuidePageView;

    /**
     * ViewFlipper
     */
    private ViewFlipper mFlipper;

    /**
     * 分类列表RecyclerView
     */
    private RecyclerView mCategoryRecyclerView;

    /**
     * 分类列表Adapter
     */
    private GuideCategoryAdapter mCategoryAdapter;

    /**
     * 性别(1:男;2:女)
     */
    private int mSex;

    /**
     * 选中男生分类列表.
     */
    private List<String> mSelManClassifyList;

    /**
     * 选中女生分类列表.
     */
    private List<String> mSelWomanClassifyList;

    /**
     * 阅读品味信息.
     */
    private RamCache<String> mReadingTasteCache;

    /**
     * 阅读品味JSON对象.
     */
    private JSONObject mReadingTasteJSONObj;

    /**
     * 是否为我的Tab进入的阅读口味界面.
     */
    private boolean isMineSource;


    /**
     * 后续有可能功能我的界面的数据，都可以使用当前回调
     */
    public static onSexChangeListener sexChangeListener;
    public static onBackListener listener;

    /**
     * 构造方法
     */
    private StartGuideMgr() {
        try {
            //阅读品味信息.
            mReadingTasteCache = new RamCache(new File(BaseContext.getContext().getFilesDir(), "novel/app/reading_taste.dat"), new StringParser());
            if (!StringFormat.isEmpty(mReadingTasteCache.get(""))) {
                mReadingTasteJSONObj = new JSONObject(mReadingTasteCache.get(""));
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "StartGuideMgr: ", throwable);
        }
    }

    /**
     * 创建当前类单例对象
     */
    private static void createInstance() {
        if (sInstance == null) {
            synchronized (StartGuideMgr.class) {
                if (sInstance == null) {
                    sInstance = new StartGuideMgr();
                }
            }
        }
    }

    public static void clearData() {
        createInstance();
        sInstance.mReadingTasteCache.set(null);
        sInstance.mReadingTasteJSONObj = null;
    }

    /**
     * 展示引导页面.
     * 默认不显示男女性别选择界面2019-08-21
     *
     * @param homeActivity
     * @param isForce      是否强制显示.
     */
    public static boolean showGuidePage(FragmentActivity homeActivity, boolean isForce) {
        if (!isForce) {
            currPageId = PageNameConstants.BOOK_CITY;
        } else {
            currPageId = PageNameConstants.MINE;
        }
        //创建当前类对象.
        createInstance();
        //判断是否需要展示阅读品味设置页.
        if (!isForce && sInstance.mReadingTasteJSONObj != null && sInstance.mReadingTasteJSONObj.optLong(JSON_KEY_UPLOAD_TIME, 0L) > 0) {
            //已设置过阅读品味.
            return false;
        }
        //清理选择的男生分类列表.
        if (sInstance.mSelManClassifyList != null) {
            sInstance.mSelManClassifyList.clear();
        }
        //清理选择的女生分类列表.
        if (sInstance.mSelWomanClassifyList != null) {
            sInstance.mSelWomanClassifyList.clear();
        }
        //判断性别页面是否显示返回按钮.
        if (isForce) {
            //显示Toolbar.
            homeActivity.findViewById(R.id.sg_back_layout).setVisibility(View.VISIBLE);
            //设置返回按钮点击事件.
            homeActivity.findViewById(R.id.sg_back_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //隐藏阅读品味设置页面.
                    if (sInstance.mGuidePageView != null) {
                        sInstance.mGuidePageView.setVisibility(View.GONE);
                    }
                    if (listener != null) {
                        listener.onBack();
                    }
                }
            });
            //初始化
            sInstance.initSettingCategory();
        }
        //设置是否来源于我的Tab.
        sInstance.isMineSource = isForce;
        //判断是否需要展示引导页面.
        sInstance.mGuidePageView = homeActivity.findViewById(R.id.guide_page_id);
        //显示引导页.
        sInstance.mGuidePageView.setVisibility(View.VISIBLE);
        //拦截点击事件.
        sInstance.mGuidePageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        //获取ViewFlipper
        sInstance.mFlipper = homeActivity.findViewById(R.id.guide_flipper);
        //设置到第一页.
        sInstance.mFlipper.setDisplayedChild(0);
        //初始化性别选择页.
        sInstance.initSexPage(homeActivity);
        //只有来源为我的Tab才显示分类选择页面.
        if (sInstance.isMineSource) {
            //初始化分类选择页.
            sInstance.initGuideClassifyPage(homeActivity);
        } else
        {
            //第一次启动应用, 显示性别选择页面.
            FuncPageStatsApi.showTastePage();
        }
        return true;
    }

    /**
     * 初始化性别选择页
     *
     * @param activity
     */
    private void initSexPage(final FragmentActivity activity) {
        //获取女生频道View.
        ImageView womanImgView = mGuidePageView.findViewById(R.id.woman_channel_imgview);
        womanImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置选中女生, 只有来源为我的Tab才显示分类选择页面.
                if (sInstance.isMineSource) {
                    updateGuideClassifyPage(SEX_WOMAN);
                    goNext();
                } else {
                    //保存阅读品味信息.
                    saveReadingTaste(activity, SEX_WOMAN, "");
                    //选择性别(女)进入主页.
                    FuncPageStatsApi.setTaste(SEX_WOMAN);
                    //第一次进入应用, 启动阅读器.
                    gotoBookRead(activity.getApplicationContext());
//                    if (isForce) FuncPageStatsApi.newUserSelectSex("2");
                }
            }
        });
        //获取男生频道View.
        ImageView manImgView = mGuidePageView.findViewById(R.id.man_channel_imgview);
        manImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置选中男生, 只有来源为我的Tab才显示分类选择页面.
                if (sInstance.isMineSource) {
                    updateGuideClassifyPage(SEX_MAN);
                    goNext();
                } else {
                    //保存阅读品味信息.
                    saveReadingTaste(activity, SEX_MAN, "");
                    //选择性别(男)进入主页.
                    FuncPageStatsApi.setTaste(SEX_MAN);
                    //第一次进入应用, 启动阅读器.
                    gotoBookRead(activity.getApplicationContext());
//                    if (isForce) FuncPageStatsApi.newUserSelectSex("1");
                }
            }
        });
    }

    public static void checkTaotiaoUpload(FragmentActivity activity) {
        String channelId = PhoneStatusManager.getInstance().getAppChannel();
        if (!TextUtils.isEmpty(channelId) && channelId.startsWith("XXL_")
                && !ToutiaoAdReport.hasUpload(activity)) {
            // 第二个是书籍id
            String[] params = channelId.split("_");
            if (params.length > 1) {
                Logger.e("toutiao", "bookid = " + params[1]);
                ActivityHelper.INSTANCE.gotoRead(activity, params[1], new BaseData("广告推广"),
                        PageNameConstants.BOOK_CITY, "");
                activity.startService(new Intent(activity, ToutiaoAdReport.class));
            }
        }
    }

    /**
     * 初始化分类选择页
     *
     * @param activity
     */
    private void initGuideClassifyPage(final FragmentActivity activity) {
        //设置返回事件.
        mGuidePageView.findViewById(R.id.back_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //返回性别选中页面.
                goLast();
            }
        });
        //开启阅读之旅按钮.
        mGuidePageView.findViewById(R.id.open_reading_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StringBuffer classifyIdBuffer = null;
                    //获取分类信息.
                    List<String> classifyIdList = mSex == 1 ? mSelManClassifyList : mSelWomanClassifyList;
                    if (classifyIdList != null && !classifyIdList.isEmpty()) {
                        for (String classifyId : classifyIdList) {
                            if (TextUtils.isEmpty(classifyId)) {
                                continue;
                            }
                            if (classifyIdBuffer == null) {
                                classifyIdBuffer = new StringBuffer();
                            } else {
                                //添加分隔符.
                                classifyIdBuffer.append(",");
                            }
                            classifyIdBuffer.append(classifyId);
                        }
                    }
                    //保存阅读品味信息.
                    saveReadingTaste(activity, mSex, StringFormat.toString(classifyIdBuffer));
                } catch (Throwable throwable) {
                    Logger.e(TAG, "onClick: 保存阅读品味信息异常:{}", throwable);
                } finally {
                    //调用资源回收接口.
                    onDestroy();
                }
            }
        });
        try {
            //获取分类列表RecyclerView
            mCategoryRecyclerView = mGuidePageView.findViewById(R.id.classify_recycler_view);
            mCategoryRecyclerView.setHasFixedSize(true);
            //设置一行显示的数量.
            GridLayoutManager layoutManager = new GridLayoutManager(activity.getApplicationContext(), 2);
            mCategoryRecyclerView.setLayoutManager(layoutManager);
            //创建Adapter.
            mCategoryAdapter = new GuideCategoryAdapter(activity, null);
            mCategoryAdapter.setOnRecyclerViewListener(new GuideCategoryAdapter.OnRecyclerViewListener() {
                @Override
                public void onItemClick(int position, CheckBox checkBox) {
                    //点击.
                    if (mCategoryAdapter != null) {
                        clickCategoryItem(mCategoryAdapter.getItemData(position), checkBox);
                    }
                }
            });
            //设置Adapter.
            mCategoryRecyclerView.setAdapter(mCategoryAdapter);
        } catch (Throwable throwable) {
            Logger.e(TAG, "initGuideClassifyPage: {}", throwable);
        }
    }

    /**
     * 更新分类数据.
     *
     * @param sex 性别(1:男;2:女)
     */
    private void updateGuideClassifyPage(final int sex) {
        mSex = sex;
        //更新数据.
        if (mCategoryAdapter != null) {
            //获取选中的分类.
//            List<String> selectCategoryIdList = mSex == SEX_MAN ? mSelManClassifyList : mSelWomanClassifyList;
//            mCategoryAdapter.setCategoryBeanList(getCategoryBeanList(mSex), StringFormat.isEmpty(selectCategoryIdList) ? null : new ArrayList(selectCategoryIdList));

            Single.fromCallable(new Callable<List<CategoryGroupBean>>() {

                @Override
                public List<CategoryGroupBean> call() throws Exception {
                    return CategoryPresenter.getCategory();
                }
            }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribeWith(new SingleObserver<List<CategoryGroupBean>>() {

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(List<CategoryGroupBean> categoryGroupBeans) {
                    if (categoryGroupBeans != null && categoryGroupBeans.size() > 0) {
                        //服务器下发选中分类标识
                        if (mSex == SEX_MAN) {
                            if (mSelManClassifyList == null) {
                                mSelManClassifyList = new ArrayList<>();
                            }
                            mSelManClassifyList.clear();
                            List<CategoryBean> categoryList = categoryGroupBeans.get(0).categoryList;
                            if (categoryList == null || categoryList.size() == 0) return;
                            for (int i = 0; i < categoryList.size(); i++) {
                                CategoryBean categoryBean = categoryList.get(i);
                                if (categoryBean == null) continue;
                                if (categoryBean.isMine() == 1) {//已选
                                    mSelManClassifyList.add(categoryBean.getId());
                                }
                            }
                        } else {
                            if (mSelWomanClassifyList == null) {
                                mSelWomanClassifyList = new ArrayList<>();
                            }
                            mSelWomanClassifyList.clear();
                            List<CategoryBean> categoryList = categoryGroupBeans.get(1).categoryList;
                            if (categoryList == null || categoryList.size() == 0) return;
                            for (int i = 0; i < categoryList.size(); i++) {
                                CategoryBean categoryBean = categoryList.get(i);
                                if (categoryBean == null) continue;
                                if (categoryBean.isMine() == 1) {//已选
                                    mSelWomanClassifyList.add(categoryBean.getId());
                                }
                            }
                        }
                        List<String> selectCategoryIdList = mSex == SEX_MAN ? mSelManClassifyList : mSelWomanClassifyList;
                        mCategoryAdapter.setCategoryBeanList(mSex == SEX_MAN ? categoryGroupBeans.get(0).categoryList : categoryGroupBeans.get(1).categoryList,
                                StringFormat.isEmpty(selectCategoryIdList) ? null : new ArrayList(selectCategoryIdList));
                    } else {
                        Logger.e(TAG, "onLoadFail: 加载分类数据失败");
                    }
                }

                @Override
                public void onError(Throwable e) {

                }
            });
        }
    }

    /**
     * 保存阅读品味信息.
     *
     * @param activity
     * @param sex         性别
     * @param classifyIds 分类列表
     */
    private void saveReadingTaste(final FragmentActivity activity, int sex, String classifyIds) {
        //性别.
        mSex = sex;
        try {
            //隐藏阅读品味设置页面.
            if (mGuidePageView != null) {
                mGuidePageView.setVisibility(View.GONE);
            }
            mReadingTasteJSONObj = new JSONObject();
            //性别.
            mReadingTasteJSONObj.put(JSON_KEY_SEX, mSex);
            //分类信息.
            mReadingTasteJSONObj.put(JSON_KEY_CATEGORY, classifyIds);
            //保存阅读品味信息.
            mReadingTasteCache.set(StringFormat.toString(mReadingTasteJSONObj));
            //调用上报阅读品味信息接口.
            updateReadingTasteInfo();
            BaseApplication.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UpgradeManager.getInstance(activity.getApplication()).startBackgroundCheck(activity, currPageId);
                    checkTaotiaoUpload(activity);
                }
            }, 1000);
            //回调性别切换监听.
            if (sexChangeListener != null) {
                sexChangeListener.isSex(mSex == SEX_MAN);
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "saveReadingTaste: {}, {}, {}, {}", activity, mSex, classifyIds, throwable);
        }
    }

    /**
     * 回到前一页
     */
    public void goLast() {
        if (mFlipper != null) {
            mFlipper.setInAnimation(BaseContext.getContext(), R.anim.guide_right_in);
            mFlipper.setOutAnimation(BaseContext.getContext(), R.anim.guide_right_out);
            mFlipper.showPrevious();
        }


    }

    /**
     * 跳到下一页
     */
    public void goNext() {
        if (mFlipper != null) {
            mFlipper.setInAnimation(BaseContext.getContext(), R.anim.guide_left_in);
            mFlipper.setOutAnimation(BaseContext.getContext(), R.anim.guide_left_out);
            mFlipper.showNext();
        }
    }

    /**
     * 点击分类.
     *
     * @param categoryBean
     * @param checkBox
     */
    private void clickCategoryItem(CategoryBean categoryBean, CheckBox checkBox) {
        if (categoryBean == null || checkBox == null) {
            Logger.e(TAG, "clickCategoryItem: {}, {}", categoryBean, checkBox);
            return;
        }
        //修改CheckBox状态.
//        checkBox.setChecked(!checkBox.isChecked());
      /*  if (!categoryBean.isSelected()){
            categoryBean.setSelected(true);
        }else {
            categoryBean.setSelected(false);
        }
        mCategoryAdapter.notifyDataSetChanged();*/
        //获取分类Id.
        String categoryId = categoryBean.getId();
        if (TextUtils.isEmpty(categoryId)) return;
        Logger.i(TAG, "clickCategoryItem: {}, {}, {}", mSex, categoryBean.getName(), checkBox.isChecked());
        if (mSex == SEX_MAN) {
            //男生
//            if (!categoryBean.isSelected()) {
//                if (mSelManClassifyList != null && !TextUtils.isEmpty(categoryId)) {
//                    mSelManClassifyList.remove(categoryId);
//                }
//                return;
//            }

            if (mSelManClassifyList.contains(categoryId)) {
                mSelManClassifyList.remove(categoryId);
                categoryBean.setSelected(false);
            } else {
                if (mSelManClassifyList.size() >= 5) {
                    Logger.e(TAG, "onCheckedChanged: 最多只能选中5个 {}", mSex);
                    ToastUtils.show(ViewUtils.getString(R.string.choose_classify_limit));
                } else {
                    mSelManClassifyList.add(categoryId);
                    categoryBean.setSelected(true);
                }
            }
            mCategoryAdapter.notifyDataSetChanged();
//            if (!categoryBean.isSelected()) {
//                categoryBean.setSelected(true);
//            } else {
//                categoryBean.setSelected(false);
//            }
//            mCategoryAdapter.notifyItemChanged(mSelManClassifyList.indexOf(categoryBean));
////            if (mSelManClassifyList == null) {
////                mSelManClassifyList = new ArrayList<>();
////            } else
//
//            if (mSelManClassifyList.size() >= 5) {
//                Logger.e(TAG, "onCheckedChanged: 最多只能选中5个 {}", mSex);
//                ToastUtils.show(ViewUtils.getString(R.string.choose_classify_limit));
//                categoryBean.setSelected(false);
//                return;
//            }
//            if (TextUtils.isEmpty(categoryId) || mSelManClassifyList.contains(categoryId)) {
//                return;
//            }
//
//            mSelManClassifyList.add(categoryId);
        } else {
            //女生
//            if (!categoryBean.isSelected()) {
//                if (mSelWomanClassifyList != null && !TextUtils.isEmpty(categoryId)) {
//                    mSelWomanClassifyList.remove(categoryId);
//                }
//                return;
//            }

//            if (mSelWomanClassifyList == null) {
//                mSelWomanClassifyList = new ArrayList<>();
//            } else

            if (mSelWomanClassifyList.contains(categoryId)) {
                mSelWomanClassifyList.remove(categoryId);
                categoryBean.setSelected(false);
            } else {
                if (mSelWomanClassifyList.size() >= 5) {
                    Logger.e(TAG, "onCheckedChanged: 最多只能选中5个 {}", mSex);
                    ToastUtils.show(ViewUtils.getString(R.string.choose_classify_limit));
                } else {
                    mSelWomanClassifyList.add(categoryId);
                    categoryBean.setSelected(true);
                }
            }
//            if (!categoryBean.isSelected()) {
//                mSelWomanClassifyList.add(categoryId);
//                categoryBean.setSelected(true);
//            } else {
//                mSelWomanClassifyList.remove(categoryId);
//                categoryBean.setSelected(false);
//            }


            mCategoryAdapter.notifyDataSetChanged();
//            if (TextUtils.isEmpty(categoryId) || mSelWomanClassifyList.contains(categoryId)) {
//                return;
//            }

        }

    }

    /**
     * 更新阅读品味信息.
     */
    public static void updateReadingTasteInfo() {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext())) {
            //无网络.
            ToastUtils.show(R.string.toast_no_net);
            return;
        }
        //创建当前类对象.
        createInstance();
        try {
            //判断阅读品味信息是否为空.
            if (sInstance.mReadingTasteJSONObj == null || sInstance.mReadingTasteJSONObj.optLong(JSON_KEY_UPLOAD_TIME, 0L) > 0) {
                //阅读品味信息已上传.
                return;
            }
            Logger.i(TAG, "updateReadingTasteInfo: {}, {}", "begin", sInstance.mReadingTasteJSONObj);
            new JsonPost.AsyncPost<ReadingTasteResp>().setRequest(new ReadingTasteReq(sInstance.mReadingTasteJSONObj)).setResponseType(ReadingTasteResp.class)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).post(new DisposableObserver<JsonResponse<ReadingTasteResp>>() {
                @Override
                protected void onStart() {
                    super.onStart();
                    Logger.i(TAG, "updateReadingTasteInfo: onStart: ");
                }

                @Override
                public void onNext(JsonResponse<ReadingTasteResp> jsonResponse) {
                    if (jsonResponse != null && jsonResponse.status == 1) {
                        try {
                            //阅读品味上报成功, 移除本地缓存信息.
                            sInstance.mReadingTasteJSONObj.put(JSON_KEY_UPLOAD_TIME, TimeTool.currentTimeMillis());
                            //保存阅读品味数据.
                            sInstance.mReadingTasteCache.set(sInstance.mReadingTasteJSONObj.toString());
                            //获取选择的性别.
                            int sex = sInstance.mReadingTasteJSONObj.getInt(JSON_KEY_SEX);
                            //更新用户对应性别信息.
                            UserInfo userInfo = UserManager.getInstance().getUserInfo();
                            if (userInfo != null) {
                                userInfo.sex = sex;
                                UserManager.getInstance().setUserInfo(userInfo);
                                if (sInstance.isMineSource) {
                                    FuncPageStatsApi.newUserSelectSex(String.valueOf(userInfo.sex));
                                }
                            }
                            //刷新书城、分类等页面数据.
                            EventBus.getDefault().post(new ReadingTasteEvent(sex));
                        } catch (Throwable throwable) {
                            Logger.e(TAG, "updateReadingTasteInfo: onNext: {}", throwable);
                            EventBus.getDefault().post(new ReadingTasteEvent(SEX_MAN));
                        }
                    }
                    Logger.i(TAG, "updateReadingTasteInfo: onNext: {}", jsonResponse);
                }

                @Override
                public void onComplete() {
                    Logger.i(TAG, "updateReadingTasteInfo: onComplete: ");
                }

                @Override
                public void onError(Throwable e) {
                    Logger.i(TAG, "updateReadingTasteInfo: onError: {}", e);
                }
            });
        } catch (Throwable throwable) {
            Logger.e(TAG, "updateReadingTasteInfo: {}", throwable);
            return;
        }
    }

    /**
     * 获取阅读品味性别.
     *
     * @return
     */
    public static int getChooseSex() {
        //创建当前类对象.
        createInstance();
        //获取UserInfo.
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        if (userInfo != null && userInfo.sex > 0) {
            return userInfo.sex;
        }
        return sInstance.mReadingTasteJSONObj != null ? sInstance.mReadingTasteJSONObj.optInt(JSON_KEY_SEX, 0) : 0;
    }

    /**
     * 判断是否选择了性别.
     *
     * @return
     */
    public static boolean isChooseSex() {
        //创建当前类对象.
        createInstance();
        return sInstance.mReadingTasteJSONObj != null;
    }

    /**
     * 初始化设置的阅读品味分类
     *
     * @return
     */
    private void initSettingCategory() {
        Logger.i(TAG, "initSettingCategory: {}", mReadingTasteJSONObj);
        //获取已设置阅读品味信息.
        String categoryIds = mReadingTasteJSONObj != null ? mReadingTasteJSONObj.optString(JSON_KEY_CATEGORY, "") : "";
        if (StringFormat.isEmpty(categoryIds)) {
            return;
        }
        try {
            if (mReadingTasteJSONObj.optInt(JSON_KEY_SEX) == SEX_WOMAN) {
                //女生.
                mSelWomanClassifyList = StringFormat.stringArrayConvertList(categoryIds.split(","));
            } else {
                //男生.
                mSelManClassifyList = StringFormat.stringArrayConvertList(categoryIds.split(","));
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "initSettingCategory: {}", throwable);
        }
    }

    /**
     * 点击返回键.
     *
     * @return
     */
    public static boolean onBackPressed() {

        if (listener != null) {
            listener.onBack();
        }

        if (sInstance == null || !sInstance.isMineSource) {
            //只有来源于我的Tab才需要处理.
            return false;
        }
        if (sInstance.mGuidePageView == null || sInstance.mGuidePageView.getVisibility() != View.VISIBLE) {
            //未显示阅读口味页面.
            return false;
        }
        if (sInstance.mFlipper.getDisplayedChild() > 0) {
            //回退到上一页.
            sInstance.goLast();
        } else {
            //关闭阅读口味页面.
            sInstance.mGuidePageView.setVisibility(View.GONE);
        }
        return true;
    }

    /**
     * 进入书籍阅读页面.
     * @param context
     */
    private void gotoBookRead(Context context)
    {
        try
        {
            Intent readIntent = new Intent(context, ReadActivity.class);
            readIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            readIntent.putExtra(ParamKey.BOOK_ID, "27149");
            readIntent.putExtra(BaseActivity.DATA_KEY, new BaseData("启动进入"));
            readIntent.putExtra(RouterPath.KEY_PARENT_ID, PageNameConstants.START_ENTER);
            //设置来源为:启动进入
            readIntent.putExtra(RouterPath.KEY_SOURCE, PageNameConstants.SOURCE_START_ENTER);
            context.startActivity(readIntent);
        } catch (Throwable throwable)
        {
            if (throwable != null)
            {
                throwable.printStackTrace();
            }
        }
    }

    /**
     * 资源回收.
     */
    private void onDestroy() {

    }


    public static void onXDestroy() {
        if (sInstance != null) sInstance = null;
        if (sexChangeListener != null) sexChangeListener = null;
        if (listener != null) listener = null;
    }

    public static void setSexChangeListener(onSexChangeListener onSexChangeListener) {
        sexChangeListener = onSexChangeListener;
    }

    public static void setOnBackListener(onBackListener onBackListener) {
        listener = onBackListener;
    }

    public static void removeListener() {
        listener = null;
    }

    public interface onSexChangeListener {

        void isSex(boolean sex);

    }

    public interface onBackListener {
        void onBack();
    }
}
