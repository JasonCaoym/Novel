package com.duoyue.app.common.data;

import com.duoyue.app.bean.*;
import com.duoyue.app.common.data.response.bookshelf.BookShelfAdInfoResp;
import com.duoyue.app.common.data.response.bookshelf.BookShelfRecoInfoResp;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.cache.GsonParser;
import com.duoyue.lib.base.cache.RamCache;
import com.duoyue.lib.base.cache.StringParser;
import com.duoyue.mianfei.xiaoshuo.data.bean.SignBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.List;

public class DataCacheManager {

    private final Gson mGson;
    private final RamCache<SearchV2MoreListBean> mSearchV2MoreListBeanRamCache;
    private final RamCache<SearchV2ListBean> mSearchV2ListBeanRamCache;
    private final RamCache<BookBannerAdBean> mManBookBannerAdBeanRamCache;
    private final RamCache<BookBannerAdBean> mBookBannerAdBeanRamCache;
    private final RamCache<BookBannerAdBean> mWomanBookBannerAdBeanRamCache;
    private final RamCache<BookCityListBean> mJxListRamCache;
    private final RamCache<BookCityListBean> mManListBeanRamCache;
    private final RamCache<BookCityListBean> mWomanListBeanRamCache;
    private final RamCache<String> mBookShelfAdInfoRespRamCache;
    private final RamCache<SignBean> mSignBeanRamCache;

    private static class Holder {
        private static final DataCacheManager INSTANCE = new DataCacheManager();
    }

    public static DataCacheManager getInstance() {
        return Holder.INSTANCE;
    }


    private final String PATH_BOOKSHELF_RECOMMEND = "novel/user/bookshelf/recommend";
    private final String PATH_BOOKSHELF_RECOMMEND_AD = "novel/user/bookshelf/recommendad";
    private final String PATH_BOOKSHELF_SIGN_INFO = "novel/user/bookshelf/signinfo";
    private final String PATH_CATEGORY_LEFT_LIST = "novel/user/category/leftlist";
    private final String PATH_CATEGORY_RIGHT_LIST = "novel/user/category/rightlist";
    private final String PATH_SEARCH_DATA = "novel/user/search/data";
    private final String PATH_SEARCH_DATA_MORE = "novel/user/search/datamore";
    private final String PATH_BOOK_CITY_JX_BANNER = "novel/user/bookcity/jx/banner";
    private final String PATH_BOOK_CITY_JX_LIST = "novel/user/bookcity/jx/list";
    private final String PATH_BOOK_CITY_MAN_BANNER = "novel/user/bookcity/man/banner";
    private final String PATH_BOOK_CITY_MAN_LIST = "novel/user/bookcity/man/list";
    private final String PATH_BOOK_CITY_WOMAN_BANNER = "novel/user/bookcity/woman/banner";
    private final String PATH_BOOK_CITY_WOMAN_LIST = "novel/user/bookcity/woman/list";

    private RamCache<BookShelfRecoInfoResp> bookShelfRecoInfoRespCache;
    private final RamCache<String> mCategoryLeftRamCache;
    private final RamCache<String> mCategoryRightRamCache;

    private DataCacheManager() {

        mGson = new Gson();

        //书架
        //每日推荐
        File recommendFile = new File(BaseContext.getContext().getFilesDir(), PATH_BOOKSHELF_RECOMMEND);
        bookShelfRecoInfoRespCache = new RamCache<>(recommendFile, new GsonParser<>(BookShelfRecoInfoResp.class));

        //滚动广告
        File recommendAdFile = new File(BaseContext.getContext().getFilesDir(), PATH_BOOKSHELF_RECOMMEND_AD);
        mBookShelfAdInfoRespRamCache = new RamCache<>(recommendAdFile, new StringParser());

        //签到信息
        File signInfoFile = new File(BaseContext.getContext().getFilesDir(), PATH_BOOKSHELF_SIGN_INFO);
        mSignBeanRamCache = new RamCache<>(signInfoFile, new GsonParser<>(SignBean.class));

        //分类左侧列表
        File categoryLeftFile = new File(BaseContext.getContext().getFilesDir(), PATH_CATEGORY_LEFT_LIST);
        mCategoryLeftRamCache = new RamCache<>(categoryLeftFile, new StringParser());

        //分类右侧列表
        File categoryRightFile = new File(BaseContext.getContext().getFilesDir(), PATH_CATEGORY_RIGHT_LIST);
        mCategoryRightRamCache = new RamCache<>(categoryRightFile, new StringParser());

        //搜索页
        File searchFile = new File(BaseContext.getContext().getFilesDir(), PATH_SEARCH_DATA);
        mSearchV2MoreListBeanRamCache = new RamCache<>(searchFile, new GsonParser<>(SearchV2MoreListBean.class));

        File searchMoreFile = new File(BaseContext.getContext().getFilesDir(), PATH_SEARCH_DATA_MORE);
        mSearchV2ListBeanRamCache = new RamCache<>(searchMoreFile, new GsonParser<>(SearchV2ListBean.class));

        //书城页

        //精选频道
        // 书城banner广告
        File bookCityBannerFile = new File(BaseContext.getContext().getFilesDir(), PATH_BOOK_CITY_JX_BANNER);
        mBookBannerAdBeanRamCache = new RamCache<>(bookCityBannerFile, new GsonParser<>(BookBannerAdBean.class));
        //书城列表数据
        File jxListFile = new File(BaseContext.getContext().getFilesDir(), PATH_BOOK_CITY_JX_LIST);
        mJxListRamCache = new RamCache<>(jxListFile, new GsonParser<>(BookCityListBean.class));

        //男频
        // 书城banner广告
        File bookCityManBannerFile = new File(BaseContext.getContext().getFilesDir(), PATH_BOOK_CITY_MAN_BANNER);
        mManBookBannerAdBeanRamCache = new RamCache<>(bookCityManBannerFile, new GsonParser<>(BookBannerAdBean.class));
        //书城列表数据
        File manListFile = new File(BaseContext.getContext().getFilesDir(), PATH_BOOK_CITY_MAN_LIST);
        mManListBeanRamCache = new RamCache<>(manListFile, new GsonParser<>(BookCityListBean.class));

        //女频
        // 书城banner广告
        File bookCityWoManBannerFile = new File(BaseContext.getContext().getFilesDir(), PATH_BOOK_CITY_WOMAN_BANNER);
        mWomanBookBannerAdBeanRamCache = new RamCache<>(bookCityWoManBannerFile, new GsonParser<>(BookBannerAdBean.class));
        //书城列表数据
        File womanListFile = new File(BaseContext.getContext().getFilesDir(), PATH_BOOK_CITY_WOMAN_LIST);
        mWomanListBeanRamCache = new RamCache<>(womanListFile, new GsonParser<>(BookCityListBean.class));

    }

    public synchronized void setBookShelfRecoInfoResp(BookShelfRecoInfoResp bookShelfRecoInfoResp) {
        bookShelfRecoInfoRespCache.set(bookShelfRecoInfoResp);
    }

    public synchronized BookShelfRecoInfoResp getBookShelfRecoInfoResp() {
        return bookShelfRecoInfoRespCache.get();
    }

    public synchronized void setBookShelfAdInfoResp(List<BookShelfAdInfoResp> bookShelfAdInfoResp) {
        mBookShelfAdInfoRespRamCache.set(mGson.toJson(bookShelfAdInfoResp));
    }

    public synchronized List<BookShelfAdInfoResp> getBookShelfAdInfoResp() {
        String s = mBookShelfAdInfoRespRamCache.get();
        List<BookShelfAdInfoResp> bookShelfAdInfoResps = mGson.fromJson(s, new TypeToken<List<BookShelfAdInfoResp>>() {
        }.getType());
        return bookShelfAdInfoResps;
    }

    public synchronized void setSignBean(SignBean signBean) {
        mSignBeanRamCache.set(signBean);
    }

    public synchronized SignBean getSignBean() {
        return mSignBeanRamCache.get();
    }

    public synchronized void setCategoryLeft(List<BookCategoryListBean> bookCategoryListBeans) {
        mCategoryLeftRamCache.set(mGson.toJson(bookCategoryListBeans));
    }

    public synchronized List<BookCategoryListBean> getCategoryLeft() {
        String s = mCategoryLeftRamCache.get();
        List<BookCategoryListBean> bookCategoryListBeans = mGson.fromJson(s, new TypeToken<List<BookCategoryListBean>>() {
        }.getType());
        return bookCategoryListBeans;
    }

    public synchronized void setCategoryRight(List<CategoryGroupBean> categoryLeft) {
        mCategoryRightRamCache.set(mGson.toJson(categoryLeft));
    }

    public synchronized List<CategoryGroupBean> getCategoryRight() {
        String s = mCategoryRightRamCache.get();
        List<CategoryGroupBean> categoryGroupBeans = mGson.fromJson(s, new TypeToken<List<CategoryGroupBean>>() {
        }.getType());
        return categoryGroupBeans;
    }

    public synchronized void setSearchV2MoreListBean(SearchV2MoreListBean searchV2MoreListBean) {
        mSearchV2MoreListBeanRamCache.set(searchV2MoreListBean);
    }

    public synchronized SearchV2MoreListBean getSearchV2MoreListBean() {
        return mSearchV2MoreListBeanRamCache.get();
    }

    public synchronized void setSearchV2ListBean(SearchV2ListBean searchV2ListBean) {
        mSearchV2ListBeanRamCache.set(searchV2ListBean);
    }

    public synchronized SearchV2ListBean getSearchV2ListBean() {
        return mSearchV2ListBeanRamCache.get();
    }

    public synchronized void setBookBannerAdBean(BookBannerAdBean bookBannerAdBean) {
        mBookBannerAdBeanRamCache.set(bookBannerAdBean);
    }

    public synchronized BookBannerAdBean getBookBannerAdBean() {
        return mBookBannerAdBeanRamCache.get();
    }

    public synchronized void setManBookBannerAdBean(BookBannerAdBean bookBannerAdBean) {
        mManBookBannerAdBeanRamCache.set(bookBannerAdBean);
    }

    public synchronized BookBannerAdBean getManBookBannerAdBean() {
        return mManBookBannerAdBeanRamCache.get();
    }

    public synchronized void setWomanBookBannerAdBean(BookBannerAdBean bookBannerAdBean) {
        mWomanBookBannerAdBeanRamCache.set(bookBannerAdBean);
    }

    public synchronized BookBannerAdBean getWomanBookBannerAdBean() {
        return mWomanBookBannerAdBeanRamCache.get();
    }

    public synchronized void setJxList(BookCityListBean bookCityListBean) {
        mJxListRamCache.set(bookCityListBean);
    }

    public synchronized BookCityListBean getJxList() {
        return mJxListRamCache.get();
    }

    public synchronized void setManList(BookCityListBean bookCityListBean) {
        mManListBeanRamCache.set(bookCityListBean);
    }

    public synchronized BookCityListBean getManList() {
        return mManListBeanRamCache.get();
    }

    public synchronized void setWomanList(BookCityListBean bookCityListBean) {
        mWomanListBeanRamCache.set(bookCityListBean);
    }

    public synchronized BookCityListBean getWomanList() {
        return mWomanListBeanRamCache.get();
    }
}
