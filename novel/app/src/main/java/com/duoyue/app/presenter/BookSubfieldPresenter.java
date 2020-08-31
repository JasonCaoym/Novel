package com.duoyue.app.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.SparseArray;
import com.duoyue.app.bean.*;
import com.duoyue.app.common.data.DataCacheManager;
import com.duoyue.app.common.data.request.bookcity.BookBannerAdReq;
import com.duoyue.app.common.data.request.bookcity.BookGuessReq;
import com.duoyue.app.common.data.request.bookcity.BookSiteListReq;
import com.duoyue.app.common.data.request.bookcity.BookSubfieldReq;
import com.duoyue.app.ui.fragment.BookRecomFragment;
import com.duoyue.app.ui.view.BookPageView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.zydm.base.common.LoadResult;
import com.zydm.base.tools.TooFastChecker;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class BookSubfieldPresenter implements BookRecomFragment.Presenter {
    private static final String TAG = "app#BookSubfieldPresenter";
    private BookPageView pageView;
    private TooFastChecker mTooFastChecker = new TooFastChecker();
    private int type;
    private String searchTitle;

    private List<Object> allData = new ArrayList<>();
    //猜你喜欢id
    private StringBuilder listId = new StringBuilder();

    private int mLike;


    private DisposableObserver bookHeaderNewListDisposable;
    private DisposableObserver bookNewListDisposable;
    private DisposableObserver loadBookSiteListDisposable;
    private DisposableObserver disposableObserver;

    public BookSubfieldPresenter(Activity activity, BookPageView pageView, int type) {
        this.pageView = pageView;
        this.type = type;
        pageView.showLoading();
        loadBannerAd(true);
    }

    //七天大礼包  会实时刷新 需要请求接口数据，但是不需要数据列表接口，isBannch
    @Override
    public void loadBannerAd(final boolean isBannch) {
        bookHeaderNewListDisposable = new DisposableObserver<JsonResponse<BookBannerAdBean>>() {
            @Override
            public void onNext(JsonResponse<BookBannerAdBean> jsonResponse) {
                mTooFastChecker.cancel();
                if (isBannch) {
                    listId.delete(0, listId.length());
                    loadPageData();
                    allData.clear();
                }

                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    BookBannerAdBean bannerAdBean = jsonResponse.data;
                    //根据频道缓存
                    switch (type) {
                        case 0://精选
                            DataCacheManager.getInstance().setBookBannerAdBean(bannerAdBean);
                            break;
                        case 1://男生
                            DataCacheManager.getInstance().setManBookBannerAdBean(bannerAdBean);
                            break;
                        case 2://女生
                            DataCacheManager.getInstance().setWomanBookBannerAdBean(bannerAdBean);
                            break;
                    }
                    pageView.showAdPage(bannerAdBean, isBannch);
//                            Logger.e(TAG, "书城Banner广告: " + new Gson().toJson(bannerAdBean));
                }

            }

            @Override
            public void onError(Throwable e) {
//                        Logger.e(TAG, "书城书城banner数据失败: " + e.getMessage());
                if (isBannch) loadPageData();
            }

            @Override
            public void onComplete() {

            }
        };

        BookBannerAdReq request = new BookBannerAdReq();
        request.setChannel(type);
        new JsonPost.AsyncPost<BookBannerAdBean>()
                .setRequest(request)
                .setResponseType(BookBannerAdBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(bookHeaderNewListDisposable);

    }

    @Override
    public void loadMoreData(int pageNum) {
        bookNewListDisposable = new DisposableObserver<JsonResponse<BookListBean>>() {
            @Override
            public void onNext(JsonResponse<BookListBean> jsonResponse) {
                mTooFastChecker.cancel();
                if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.getList() != null
                        && !jsonResponse.data.getList().isEmpty()) {
                    pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_SUCCEED);
                    //书籍去重
                    List<BookCityItemBean> bookCityItemBeanList = jsonResponse.data.getList();
                    for (BookCityItemBean cityItemBean : bookCityItemBeanList) {
//                        cityItemBean.setId(mLike);
                        listId.append(cityItemBean.getId());
                        listId.append(",");
                    }
                    pageView.showMorePage(bookCityItemBeanList);
                } else {
                    pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL_NO_DATA);
                }
            }

            @Override
            public void onError(Throwable e) {
                mTooFastChecker.cancel();
                pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL);
            }

            @Override
            public void onComplete() {

            }
        };
        BookGuessReq request = new BookGuessReq();
        request.setQuePages(pageNum);
        request.setRepeatBookId(listId.deleteCharAt(listId.length() - 1).toString());
        new JsonPost.AsyncPost<BookListBean>()
                .setRequest(request)
                .setResponseType(BookListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(bookNewListDisposable);
    }

    @Override
    public void loadPageData() {
        loadBookSiteListDisposable = new DisposableObserver<JsonResponse<BookCityListBean>>() {
            @Override
            public void onNext(JsonResponse<BookCityListBean> jsonResponse) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_SUCCEED);

                if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.getList() != null
                        && !jsonResponse.data.getList().isEmpty()) {

                    BookCityListBean bookCityListBean = jsonResponse.data;
                    //根据频道缓存
                    switch (type) {
                        case 0://精选
                            DataCacheManager.getInstance().setJxList(bookCityListBean);
                            break;
                        case 1://男生
                            DataCacheManager.getInstance().setManList(bookCityListBean);
                            break;
                        case 2://女生
                            DataCacheManager.getInstance().setWomanList(bookCityListBean);
                            break;
                    }
                    setListData(bookCityListBean);
                    // 处理搜索栏提示语
                    searchTitle = jsonResponse.data.getSearchTitle();
                    pageView.showPage(allData);
                } else {
                    pageView.showEmpty();
                }
            }

            @Override
            public void onError(Throwable e) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                if (allData.isEmpty()) {
                    pageView.showNetworkError();
                } else {
                    pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_FAIL);
                }
            }

            @Override
            public void onComplete() {

            }
        };
        BookSubfieldReq request = new BookSubfieldReq();
        request.setType(type);
        new JsonPost.AsyncPost<BookCityListBean>()
                .setRequest(request)
                .setResponseType(BookCityListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(loadBookSiteListDisposable);
    }

    public List<Object> setListData(BookCityListBean bookCityListBean) {
        List<BookCityModuleBean> list = bookCityListBean.getList();
        if (list != null && list.size() > 0) {
            list.get(list.size() - 1).setLastPosition(true);
        }
        //上报id
        saveId(bookCityListBean);

        for (BookCityModuleBean item : bookCityListBean.getList()) {
            // 丢弃空数据
            if (item == null) continue;
            if (item.getTag() == null) continue;
            if (item.getChildColumns() == null) continue;
            //人气榜，飙升榜，完结榜
            switch (item.getStyle()) {
                case BookCityModuleBean.ONE:
                    BookRankingColumnBean bookRankingColumnBean = new BookRankingColumnBean();
                    item.setType(type);
                    bookRankingColumnBean.setBookCityModuleBean(item);
                    allData.add(bookRankingColumnBean);
                    break;
                case BookCityModuleBean.ONE_2_FOUR:
                    BookOne2MoreBean bookOne2MoreBean = new BookOne2MoreBean();
                    item.setType(type);
                    bookOne2MoreBean.setBookCityModuleBean(item);
                    allData.add(bookOne2MoreBean);
                    break;
                case BookCityModuleBean.ONE_V2_FOUR:
                    BookOne2FourBean bookOne2FourBean = new BookOne2FourBean();
                    item.setType(type);
                    bookOne2FourBean.setBookCityModuleBean(item);
                    allData.add(bookOne2FourBean);
                    break;
                case BookCityModuleBean.THREE:
                    BookThreeBean bookThreeBean = new BookThreeBean();
                    if (TextUtils.equals(item.getTag(), "CNXHJN") || TextUtils.equals(item.getTag(), "CNXHJV")) {
                        List<BookCityItemBean> books = item.getChildColumns().get(0).getBooks();
                        if (books != null) {
                            mLike = Integer.valueOf(item.getId());
                            if (books.size() > 5) {
                                List<BookCityItemBean> bookCityItemBeanList = books.subList(0, 5);
                                item.getChildColumns().get(0).setBooks(bookCityItemBeanList);
                            } else {
                                item.getChildColumns().get(0).setBooks(books);
                            }
                        }
                    }
                    bookThreeBean.setBookCityModuleBean(item);
                    item.setType(type);
                    allData.add(bookThreeBean);
                    break;
                case BookCityModuleBean.ONE_2_DOUBLE:
                    BookOne2DoubleBean bookOne2DoubleBean = new BookOne2DoubleBean();
                    item.setType(type);
                    bookOne2DoubleBean.setBookCityModuleBean(item);
                    allData.add(bookOne2DoubleBean);
                    break;

                case BookCityModuleBean.ONE_N:
                    BookNBean bookNBean = new BookNBean();
                    item.setType(type);
                    bookNBean.setBookCityModuleBean(item);
                    allData.add(bookNBean);
                    break;
            }
        }

        return allData;
    }


    void saveId(BookCityListBean bookCityListBean) {
        SparseArray<List<Long>> sparseArray = new SparseArray<>();

        for (BookCityModuleBean bean : bookCityListBean.getList()) {
            if (bean.getTag() == null) continue;

            if (bean.getChildColumns() == null) continue;
            // 男排行榜 女排行榜 都不需要上传id
            if (bean.getTag().equals("JXNPHB") || bean.getTag().equals("JXVPHB")) continue;
            //猜你喜欢男  女
            if (bean.getTag().equals("CNXHJN") || bean.getTag().equals("CNXHJV")) {
                if (bean.getChildColumns().get(0).getBooks() != null) {
                    for (BookCityItemBean bookCityItemBean : bean.getChildColumns().get(0).getBooks()) {
                        if (bookCityItemBean != null) {
                            listId.append(bookCityItemBean.getId());
                            listId.append(",");
                        }
                    }
                }
                continue;
            }
            List<Long> longList = new ArrayList<>();
            for (BookChildColumnsBean bookChildColumnsBean : bean.getChildColumns()) {
                if (bookChildColumnsBean.getBooks() == null) continue;
                for (BookCityItemBean bookCityItemBean : bookChildColumnsBean.getBooks()) {
                    if (bookCityItemBean != null) {
                        longList.add(bookCityItemBean.getId());
                    }
                }
            }
            sparseArray.put(Integer.valueOf(bean.getId()), longList);
        }
        BookCityChangeBean.getInstance().getSparseArray().put(type, sparseArray);

//        SparseArray<List<Long>> listSparseArray = BookCityChangeBean.getInstance().getSparseArray().get(type);
//        for (int i = 0; i < listSparseArray.size(); i++) {
//            Log.i("BookCityItemBean", listSparseArray.keyAt(i) + "<---->" + listSparseArray.valueAt(i));
//        }
    }


//    public void loadBagDataList() {
//        BookBagListReq request = new BookBagListReq();
//        new JsonPost.AsyncPost<BookBagListBean>()
//                .setRequest(request)
//                .setResponseType(BookBagListBean.class)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .post(new DisposableObserver<JsonResponse<BookBagListBean>>() {
//                    @Override
//                    public void onNext(JsonResponse<BookBagListBean> jsonResponse) {
//                        if (jsonResponse.status == 1 && jsonResponse.data != null) {
//                            pageView.loadBagList(jsonResponse.data);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        //此处不需要回调 如果书城页面数据展示 防止7日活动接口返回失败最终影响整个界面
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

    public void loadBookSiteList(int site, int chan) {
        disposableObserver = new DisposableObserver<JsonResponse<BookSiteBean>>() {
            @Override
            public void onNext(JsonResponse<BookSiteBean> jsonResponse) {
                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    pageView.loadSiteData(jsonResponse.data);
                }
            }

            @Override
            public void onError(Throwable e) {
                //此处不需要回调 如果书城页面数据展示 防止悬浮广告接口返回失败最终影响整个界面
            }

            @Override
            public void onComplete() {

            }
        };
        BookSiteListReq bookSiteListReq = new BookSiteListReq();
        bookSiteListReq.site = site;
        bookSiteListReq.chan = chan;

        new JsonPost.AsyncPost<BookSiteBean>()
                .setRequest(bookSiteListReq)
                .setResponseType(BookSiteBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(disposableObserver);
    }

    public void onDistory() {
        if (bookHeaderNewListDisposable != null && !bookHeaderNewListDisposable.isDisposed()) {
            bookHeaderNewListDisposable.dispose();
        }
        if (bookNewListDisposable != null && !bookNewListDisposable.isDisposed()) {
            bookNewListDisposable.dispose();
        }
        if (loadBookSiteListDisposable != null && !loadBookSiteListDisposable.isDisposed()) {
            loadBookSiteListDisposable.dispose();
        }
        if (disposableObserver != null && !disposableObserver.isDisposed()) {
            disposableObserver.dispose();
        }
    }

    @Override
    public String getSearchTitle() {
        return searchTitle;
    }
}
