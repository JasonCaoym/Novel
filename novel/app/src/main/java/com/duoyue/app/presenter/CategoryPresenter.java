package com.duoyue.app.presenter;

import android.util.Log;

import com.duoyue.app.bean.CategoryAllGroupBean;
import com.duoyue.app.bean.CategoryGroupBean;
import com.duoyue.app.common.data.request.category.CategoryReq;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.ui.view.NewCategoryNotificationView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 分类
 *
 * @author caoym
 * @data 2019/4/19  14:56
 */
public class CategoryPresenter {


    private NewCategoryNotificationView categoryView;

    private CategoryPresenter() {
    }

    public CategoryPresenter(NewCategoryNotificationView newCategoryView) {
        this.categoryView = newCategoryView;
    }

    public static List<CategoryGroupBean> getCategory() {
        //男生分类列表.
        CategoryReq request = new CategoryReq();
        request.parentId = 0;
        JsonPost.SyncPost<CategoryAllGroupBean> post = new JsonPost.SyncPost<CategoryAllGroupBean>()
                .setRequest(request)
                .setResponseType(CategoryAllGroupBean.class);
        try {
            JsonResponse<CategoryAllGroupBean> jsonResponse = post.post();
            if (jsonResponse.status == 1 && jsonResponse.data != null) {
                List<CategoryGroupBean> categoryGroupList = new ArrayList<>();

                CategoryGroupBean maleBean = new CategoryGroupBean();
                maleBean.categoryList = jsonResponse.data.getMaleBean();
                maleBean.groupId = StartGuideMgr.SEX_MAN;
                maleBean.groupName = ViewUtils.getString(R.string.male);

                CategoryGroupBean femaleBean = new CategoryGroupBean();
                femaleBean.categoryList = jsonResponse.data.getFemaleBean();
                femaleBean.groupId = StartGuideMgr.SEX_WOMAN;
                femaleBean.groupName = ViewUtils.getString(R.string.female);

                CategoryGroupBean bookBean = new CategoryGroupBean();
                bookBean.categoryList = jsonResponse.data.getPushBean();
                bookBean.groupId = StartGuideMgr.BOOK;
                bookBean.groupName = ViewUtils.getString(R.string.book);

                categoryGroupList.add(maleBean);
                categoryGroupList.add(femaleBean);
                categoryGroupList.add(bookBean);

                return categoryGroupList;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    //通知栏下发的二级分类
    public void loadCategory(final int pid) {
        CategoryReq request = new CategoryReq();
        request.parentId = pid;
        new JsonPost.AsyncPost<CategoryAllGroupBean>()
                .setRequest(request)
                .setResponseType(CategoryAllGroupBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(new DisposableObserver<JsonResponse<CategoryAllGroupBean>>() {
                    @Override
                    public void onNext(JsonResponse<CategoryAllGroupBean> jsonResponse) {
                        if (jsonResponse.status == 1 && jsonResponse.data != null) {
                            CategoryAllGroupBean bean = jsonResponse.data;
                            switch (pid) {
                                case 1:
                                    categoryView.updateCategory(bean.getMaleBean());
                                    break;
                                case 2:
                                    categoryView.updateCategory(bean.getFemaleBean());
                                    break;
                                case 3:
                                    categoryView.updateCategory(bean.getPushBean());
                                    break;
                                default:
                                    categoryView.updateCategory(null);
                                    break;
                            }
                        } else {
                            categoryView.updateCategory(null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        categoryView.showNetworkError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
