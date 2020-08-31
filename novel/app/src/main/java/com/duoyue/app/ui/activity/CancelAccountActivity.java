package com.duoyue.app.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.CancelAccountPresenter;
import com.duoyue.lib.base.app.user.ILoginContact;
import com.duoyue.lib.base.app.user.LoginPresenter;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.widget.SimpleDialog;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 注销账号页面.
 * @author caoym
 * @data 2020/3/25  17:57
 */
public class CancelAccountActivity extends BaseActivity
{
    /**
     * 日志Tag
     */
    private static final String LOG_TAG = "App#CancelAccountActivity";

    /**
     * 注销账号View.
     */
    private View mCancelAccountView;

    private TextView mCancelAccountTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_account);
        //标题.
        ((TextView) findViewById(R.id.toolbar_title)).setText("注销账号");
        //注销按钮.
        mCancelAccountView = findViewById(R.id.cancel_account_btn);
        mCancelAccountTextView = findViewById(R.id.cancel_account_textview);
        mCancelAccountView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Logger.e(LOG_TAG, "点击注销账号.");
                //注销账号.
                cancelAccount();
            }
        });
    }

    /**
     * 注销账号.
     */
    private void cancelAccount()
    {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext())) {
            Toast.makeText(getApplicationContext(), "网络不可用, 请稍后再试...", Toast.LENGTH_LONG).show();
            return;
        }
        final CancelAccountPresenter presenter = new CancelAccountPresenter(new ILoginContact.ILoginView()
        {
            @Override
            public void onLoginStart()
            {
                Logger.i(LOG_TAG, "cancelAccount start.");
                //开始注销账号.
                if (mCancelAccountView != null)
                {
                    mCancelAccountView.setEnabled(false);
                }
                if (mCancelAccountTextView != null)
                {
                    mCancelAccountTextView.setText(R.string.cancel_account_ing);
                }
            }
            @Override
            public void onLoginCancel()
            {

            }
            @Override
            public void onLoginSuccess(UserInfo info)
            {
                Logger.i(LOG_TAG, "cancelAccount success.");
                ToastUtils.show("注销成功");
                try
                {
                    //发送登录成功广播.--将数据切换到游客
                    Intent intent = new Intent(Constants.LOGIN_SUCC_ACTION);
                    BaseContext.getContext().sendBroadcast(intent);
                } catch (Throwable throwable) {
                    Logger.e(LOG_TAG, "cancelAccount success: {}, {}", info, throwable);
                }
                //返回处理结果.
                setResult(200);
                finish();
            }
            @Override
            public void onLoginFailure(String msg)
            {
                Logger.e(LOG_TAG, "cancelAccount failure:{}.", msg);
                //注销账号失败.
                cancelAccountFail();
            }
            @Override
            public void onLoginError(Throwable throwable)
            {
                Logger.e(LOG_TAG, "cancelAccount error:{}.", throwable);
                //注销账号失败.
                cancelAccountFail();
            }
        });

        try {
            SimpleDialog simpleDialog = new SimpleDialog.Builder(this).setCanceledOnTouchOutside(false).setMessage(R.string.confirm_cancel_account).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //关闭Dialog.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    //调用注销账号接口.
                    if (presenter != null)
                    {
                        presenter.cancelAccount();
                    }
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //关闭Dialog.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }).create();
            //显示Dialog.
            simpleDialog.show();
        } catch (Throwable throwable) {
            Logger.e(LOG_TAG, "cancelAccount: {}", throwable);
        }
    }

    /**
     * 注销账号失败.
     */
    private void cancelAccountFail()
    {
        //注销账号失败.
        ToastUtils.show(R.string.cancel_account_fail);
        if (mCancelAccountView != null)
        {
            mCancelAccountView.setEnabled(true);
        }
        if (mCancelAccountTextView != null)
        {
            mCancelAccountTextView.setText(R.string.apply_cancel);
        }
    }

    @NotNull
    @Override
    public String getCurrPageId()
    {
        return "CancelAccount";
    }
}
