package com.duoyue.app;

import android.net.Uri;

import com.duoyue.lib.base.app.user.ILoginContact;
import com.duoyue.lib.base.app.user.IVerifyContact;
import com.duoyue.lib.base.app.user.LoginPresenter;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.VerifyPresenter;
import com.duoyue.lib.base.crypto.NES;
import com.duoyue.lib.base.log.Logger;

import com.duoyue.mod.ad.bean.AdPositionConfigBean;
import com.duoyue.mod.ad.dao.AdPositionConfigHelp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowLog;

import java.net.InetAddress;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowLog.class, ApiTest.ShadowNES.class}, sdk = 23)
public class ApiTest
{
    private static final String TAG = "Test#ApiTest";

    @Before
    public void setUp() throws Exception
    {
        ShadowLog.stream = System.out;

        RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>()
        {
            @Override
            public Scheduler apply(Scheduler scheduler) throws Exception
            {
                return AndroidSchedulers.mainThread();
            }
        });
    }

    @Test
    public void testVerify()
    {
        new VerifyPresenter(new IVerifyContact.IVerifyView()
        {
            @Override
            public void onVerifyStart()
            {
                Logger.d(TAG, "onVerifyStart: ");
            }

            @Override
            public void onVerifyCancel()
            {
                Logger.d(TAG, "onVerifyCancel: ");
            }

            @Override
            public void onVerifySuccess()
            {
                Logger.d(TAG, "onVerifySuccess: ");
            }

            @Override
            public void onVerifyFailure(String msg)
            {
                Logger.d(TAG, "onVerifyFailure: ", msg);
            }

            @Override
            public void onVerifyError(Throwable throwable)
            {
                Logger.d(TAG, "onVerifyError: ", throwable);
            }
        }).sendVerifyCode("13480830405");
    }

    @Test
    public void testLogin()
    {
        new LoginPresenter(new ILoginContact.ILoginView()
        {
            @Override
            public void onLoginStart()
            {
                Logger.d(TAG, "onLoginStart: ");
            }

            @Override
            public void onLoginCancel()
            {
                Logger.d(TAG, "onLoginCancel: ");
            }

            @Override
            public void onLoginSuccess(UserInfo info)
            {
                Logger.d(TAG, "onLoginSuccess: ", info.uid);
            }

            @Override
            public void onLoginFailure(String msg)
            {
                Logger.d(TAG, "onLoginFailure: ", msg);
            }

            @Override
            public void onLoginError(Throwable throwable)
            {
                Logger.d(TAG, "onLoginError: ", throwable);
            }
        }).login();
    }

    @Test
    public void testDomain() throws Throwable
    {
        String host = Uri.parse("http://192.168.0.128:6100").getHost();
        Logger.d(TAG, "testDomain: ", host);
        InetAddress inetAddress = InetAddress.getByName(host);
        Logger.d(TAG, "testDomain: ", inetAddress.isReachable(3000));
        Logger.d(TAG, "testDomain: ", inetAddress.getHostAddress());
    }

    @Implements(NES.class)
    public static class ShadowNES
    {
        public static byte[] encode(byte[] data)
        {
            return xor(data);
        }

        public static byte[] decode(byte[] data)
        {
            return xor(data);
        }

        private static byte[] xor(byte[] data)
        {
            byte[] keys = Integer.toHexString(data.length).getBytes();
            for (int i = 0; i < data.length; i++)
            {
                data[i] = (byte) (data[i] ^ keys[i % keys.length]);
            }
            return data;
        }
    }

    @Test
    public static void databaseTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 20;
                while (i-- > 0) {
                    try {
                        List<AdPositionConfigBean> posList = AdPositionConfigHelp.getsInstance().findAvailableAdPos();
                        if (posList != null && !posList.isEmpty()) {

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Logger.e("db error", "数据库错误： i = " + i + ", msg: " + ex.getMessage());
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 20;
                while (i-- > 0) {
                    try {
                        List<AdPositionConfigBean> posList = AdPositionConfigHelp.getsInstance().findAvailableAdPos();
                        if (posList != null && !posList.isEmpty()) {

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Logger.e("db error", "数据库错误： i = " + i + ", msg: " + ex.getMessage());
                    }
                }
            }
        }).start();
    }
}