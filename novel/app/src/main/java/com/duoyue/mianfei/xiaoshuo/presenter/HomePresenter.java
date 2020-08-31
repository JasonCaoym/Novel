package com.duoyue.mianfei.xiaoshuo.presenter;

import android.text.TextUtils;
import com.duoyue.app.common.data.request.bookcity.RecommandReq;
import com.duoyue.app.common.mgr.BookShelfMgr;
import com.duoyue.app.ui.view.HomeView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.app.user.SupplyDeviceRequest;
import com.duoyue.lib.base.location.BDLocationMgr;
import com.duoyue.lib.base.location.LocationModel;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.data.bean.RecommandBean;
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity;
import com.zydm.base.data.dao.BookShelfBean;
import com.zydm.base.data.dao.BookShelfHelper;
import com.zydm.base.rx.MtSchedulers;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomePresenter implements HomeActivity.Presenter {

    private static final String TAG = "App#HomePresenter";

    private final HomeView mHomeView;
    private DisposableObserver<JsonResponse<RecommandBean>> mRecommandDisposableObserver;
    private DisposableObserver<String> mDisposableObserver;
    private DisposableObserver<JsonResponse<Object>> mLocationDisposableObserver;

    public HomePresenter(HomeView homeView) {
        mHomeView = homeView;
    }

    /**
     * 检测是否符合口令规则
     *
     * @param clipText
     */
    @Override
    public void loadData(String clipText) {
        //验证口令是否符合规则(规则:[dy][开头, 中间为8~20位的字母+数字, ][yd]结束).
        String regex = "(^\\[dy\\]\\[)([A-Za-z0-9]{8,20})(\\]\\[yd\\]$)";
        Matcher matcher = Pattern.compile(regex).matcher(clipText);
        if (!matcher.find())
        {
            //非口令.
            Logger.e(TAG, "不符合口令规则.");
            return;
        }
        mRecommandDisposableObserver = new DisposableObserver<JsonResponse<RecommandBean>>() {
            @Override
            public void onNext(JsonResponse<RecommandBean> recommandBeanJsonResponse) {
                if (recommandBeanJsonResponse.status == 1 && recommandBeanJsonResponse.data != null) {
                    mHomeView.showDialog(recommandBeanJsonResponse.data);
                } else {
                    mHomeView.showEmpty();
                }
            }

            @Override
            public void onError(Throwable e) {
                mHomeView.showError();
            }

            @Override
            public void onComplete() {

            }
        };
        RecommandReq recommandReq = new RecommandReq();
        recommandReq.setCommand(clipText);

        new JsonPost.AsyncPost<RecommandBean>()
                .setRequest(recommandReq)
                .setResponseType(RecommandBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(mRecommandDisposableObserver);
    }

    /**
     * 查询有没有本地添加书架，还没有同步服务器的数据
     */
    @Override
    public void uploadAddShelf() {
        mDisposableObserver = new DisposableObserver<String>() {
            @Override
            public void onNext(String o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        Observable.fromCallable(new Callable<List<BookShelfBean>>() {
            @Override
            public List<BookShelfBean> call() throws Exception {
                List<BookShelfBean> list = BookShelfHelper.getsInstance().findLocalAddBooks();
                Logger.i(TAG, "list.size:  " + list.size());
                return list;
            }
        }).flatMap(new Function<List<BookShelfBean>, ObservableSource<BookShelfBean>>() {
            @Override
            public ObservableSource<BookShelfBean> apply(List<BookShelfBean> bookShelfBeans) throws Exception {
                return Observable.fromIterable(bookShelfBeans);
            }
        }).map(new Function<BookShelfBean, String>() {
            @Override
            public String apply(BookShelfBean bookShelfBean) throws Exception {

                Logger.i(TAG, "bookShelfBean:  " + bookShelfBean.getBookName());

                String result = BookShelfMgr.addBookShelf(bookShelfBean);

                Logger.i(TAG, "bookShelfBean add:  " + bookShelfBean.getBookName() + "  " + result);

                if (TextUtils.equals(result, BookShelfMgr.HTTP_OK)) {
                    bookShelfBean.setIsAddLocalDb(false);
                    BookShelfHelper.getsInstance().saveBook(bookShelfBean);
                    Logger.i(TAG, "bookShelfBean add:  " + bookShelfBean.getBookName() + "  HTTP_OK");
                }
                return result;
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi())
                .subscribe(mDisposableObserver);

    }

    @Override
    public void LocationModel() {
        mLocationDisposableObserver = new DisposableObserver<JsonResponse<Object>>() {
            @Override
            public void onNext(JsonResponse<Object> jsonResponse) {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        LocationModel locationModel = BDLocationMgr.getLocation();
        if (locationModel == null) return;
        if (locationModel.getmLatitude() == 0 || locationModel.getmLongitude() == 0) return;
        SupplyDeviceRequest supplyDeviceRequest = new SupplyDeviceRequest();
        new JsonPost.AsyncPost<Object>()
                .setRequest(supplyDeviceRequest)
                .setResponseType(Object.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(mLocationDisposableObserver);
    }

    @Override
    public void destroy() {
        if (mRecommandDisposableObserver != null && !mRecommandDisposableObserver.isDisposed()) {
            mRecommandDisposableObserver.dispose();
        }
        if (mDisposableObserver != null && !mDisposableObserver.isDisposed()) {
            mDisposableObserver.dispose();
        }
        if (mLocationDisposableObserver != null && mLocationDisposableObserver.isDisposed()) {
            mLocationDisposableObserver.dispose();
        }
    }
}
