package com.duoyue.app.common.mgr;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.*;
import android.widget.TextView;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.PermissionUtil;
import com.duoyue.lib.base.app.user.MobileInfoPresenter;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.devices.SystemUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.location.BDLocationMgr;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.zydm.base.utils.SPUtils;

import java.util.*;

/**
 * 权限管理
 *
 * @author caoym
 * @data 2019/5/7  10:03
 */
public class PermissionMgr {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#PermissionMgr";
    public static final String PERMISSION_REQUEST_TODAY = "permission_request_today";
    private static final String PERMISSION_STORAGE = "storage_selecte_refuse";
    private static final String PERMISSION_PHONE = "phone_selecte_refuse";
    private static final String PERMISSION_LOCATION = "location_selecte_refuse";
    /**
     * 请求授权.
     */
    private static final int REQUEST_CODE_PERMISSION = 1000;

    /**
     * 当前类对象
     */
    private static PermissionMgr sInstance;

    /**
     * 授权提示Dialog.
     */
    private AlertDialog mPermissionDialog;

    /**
     * 权限内容TextView
     */
    private TextView mPermissionTextView;

    /**
     * 要申请的权限列表.
     */
    private List<PermissionInfo> mPermissionList;

    private PermissionMgr() {
    }

    /**
     * 创建当前类单例对象
     */
    private synchronized static void createInstance() {
        if (sInstance == null) {
            synchronized (PermissionMgr.class) {
                if (sInstance == null) {
                    sInstance = new PermissionMgr();
                }
            }
        }
    }

    /**
     * 请求权限
     *
     * @param activity
     * @return
     */
    public static boolean requestPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //创建当前类对象.
            createInstance();
            //初始化需要授权的权限.
            if (Constants.IS_AIGAO) {
                sInstance.initPermission(activity);
            } else {
                sInstance.initPermission();
            }
            //检查授权.
            return sInstance.checkPermission(activity);
        }
        return true;
    }

    /**
     * @param activity
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean onRequestPermissionsResult(Activity activity, int requestCode, final String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (sInstance != null) {
                    //过滤已授权成功的权限.
                    sInstance.filterAuthPermission(true);
                    //判断是否授权未完成.
                    if (!StringFormat.isEmpty(sInstance.mPermissionList)) {
                        //授权未完成, 继续授权.
                        sInstance.confirmPermission(activity, permissions, grantResults);
                        if (Constants.IS_AIGAO) {
                            if (PhoneUtil.checkPermission(activity, permissions[0])) {
                                Logger.e(TAG, permissions[0] + "授权同意了");
                                SPUtils.INSTANCE.putBoolean(PermissionMgr.PERMISSION_REQUEST_TODAY, true);
                            } else if (activity.shouldShowRequestPermissionRationale(permissions[0])){
                                Logger.e(TAG, permissions[0] + "拒绝了，但是没有勾选不再提醒");
                            } else {
                                if (permissions != null && permissions.length > 0) {
                                    SPUtils.INSTANCE.putBoolean(PermissionMgr.PERMISSION_REQUEST_TODAY, true);
                                    if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                        SPUtils.INSTANCE.putBoolean(PERMISSION_STORAGE, true);
                                        Logger.e(TAG, "读写权限拒绝了，勾选不再提醒");
                                    } else if (permissions[0].equals(Manifest.permission.READ_PHONE_STATE)) {
                                        SPUtils.INSTANCE.putBoolean(PERMISSION_PHONE, true);
                                        Logger.e(TAG, "手机权限拒绝了，勾选不再提醒");
                                    } else if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                        SPUtils.INSTANCE.putBoolean(PERMISSION_LOCATION, true);
                                        Logger.e(TAG, "定位权限拒绝了，勾选不再提醒");
                                    }
                                }
                            }
                        }
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 初始化权限列表.
     */
    private void initPermission() {
        mPermissionList = new ArrayList<>();
        //sdcard存储--必须权限
        PermissionInfo info = new PermissionInfo();
        info.name = "存储空间";
        //info.requestMsg = "请允许应用访问文件，是否继续？";
        //info.refuseMsg = "1. 点击设置，进入应用信息页\n2. 选择\"存储空间\"\n3. 点按\"始终允许\"";
        //设置为必须权限.
        info.isRequired = true;
        //权限.
        info.permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        mPermissionList.add(info);
        //读取手机信息--必须权限
        info = new PermissionInfo();
        info.name = "电话";
        //info.requestMsg = "请允许应用拨打电话和管理通话，是否继续？";
        //info.refuseMsg = "1. 点击设置，进入应用信息页\n2. 选择\"电话\"\n3. 点按\"始终允许\"";
        //设置为必须权限.
        info.isRequired = true;
        //权限.
        info.permission = Manifest.permission.READ_PHONE_STATE;
        mPermissionList.add(info);
        //位置信息--非必须权限
        info = new PermissionInfo();
        info.name = "您的位置";
        //info.requestMsg = "请允许应用获取位置信息，是否继续？";
        //info.refuseMsg = "1. 点击设置，进入应用信息页\n2. 选择\"位置信息\"\n3. 点按\"始终允许\"";
        //设置为必须权限.
        info.isRequired = true;
        //权限.
        info.permission = Manifest.permission.ACCESS_FINE_LOCATION;
        mPermissionList.add(info);
    }

    /**
     * AIGAO渠道 -- 初始化权限列表.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initPermission(Activity activity) {
        mPermissionList = new ArrayList<>();

        PermissionInfo info;
        //读取手机信息--必须权限
        if (!PhoneUtil.checkPermission(BaseContext.getContext(), Manifest.permission.READ_PHONE_STATE)
                && !SPUtils.INSTANCE.getBoolean(PERMISSION_PHONE, false)) { // 拒绝了没有勾选不再提醒
            info = new PermissionInfo();
            info.name = "电话";
            //info.requestMsg = "请允许应用拨打电话和管理通话，是否继续？";
            //info.refuseMsg = "1. 点击设置，进入应用信息页\n2. 选择\"电话\"\n3. 点按\"始终允许\"";
            //设置为必须权限.
            info.isRequired = true;
            //权限.
            info.permission = Manifest.permission.READ_PHONE_STATE;
            mPermissionList.add(info);
        } else {
//            Logger.e(TAG, "电话权限允许了");
        }
        //sdcard存储--必须权限
        if (!PhoneUtil.checkPermission(BaseContext.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && !SPUtils.INSTANCE.getBoolean(PERMISSION_STORAGE, false)) { // 拒绝了没有勾选不再提醒
            info = new PermissionInfo();
            info.name = "存储空间";
            //info.requestMsg = "请允许应用访问文件，是否继续？";
            //info.refuseMsg = "1. 点击设置，进入应用信息页\n2. 选择\"存储空间\"\n3. 点按\"始终允许\"";
            //设置为必须权限.
            info.isRequired = true;
            //权限.
            info.permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            mPermissionList.add(info);
        } else {
//            Logger.e(TAG, "存储权限允许了");
        }
        //位置信息--非必须权限
        if (!PhoneUtil.checkPermission(BaseContext.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                && !SPUtils.INSTANCE.getBoolean(PERMISSION_LOCATION, false)) { // 拒绝了没有勾选不再提醒
            info = new PermissionInfo();
            info.name = "您的位置";
            //info.requestMsg = "请允许应用获取位置信息，是否继续？";
            //info.refuseMsg = "1. 点击设置，进入应用信息页\n2. 选择\"位置信息\"\n3. 点按\"始终允许\"";
            //设置为必须权限.
            info.isRequired = true;
            //权限.
            info.permission = Manifest.permission.ACCESS_FINE_LOCATION;
            mPermissionList.add(info);
        } else {
//            Logger.e(TAG, "定位权限允许了");
        }
    }

    /**
     * 检查权限.
     *
     * @param activity
     * @return 是否授权成功(true : 成功 ; false : 失败)
     */
    private boolean checkPermission(Activity activity) {
        //过滤已授权权限.
        filterAuthPermission(false);
        if (StringFormat.isEmpty(mPermissionList)) {
            //已授权完成.
            return true;
        }
        try {
            int permissionSize = 0;
            if (Constants.IS_AIGAO) {
                permissionSize = 1;
            } else {
                permissionSize = mPermissionList.size();
            }
            String[] permissionArray = new String[permissionSize];
            for (int index = 0; index < permissionSize; index++) {
                permissionArray[index] = mPermissionList.get(index).permission;
            }
            //发送授权请求(必须从后往前).
            ActivityCompat.requestPermissions(activity, permissionArray, REQUEST_CODE_PERMISSION);
        } catch (Throwable throwable) {
            Logger.e(TAG, "checkPermission: {}, {}", activity, throwable);
        }
        return false;
    }

    /**
     * 确认权限.
     *
     * @param activity
     * @param permissions
     * @param grantResults
     */
    private void confirmPermission(Activity activity, String[] permissions, int[] grantResults) {
        //禁止授权列表.
        List<String> forbidPermissionList = new ArrayList<>();
        //禁止且不再提醒授权列表.
        List<String> notTemindPermissionList = new ArrayList<>();
        //遍历下一个授权弹框.
        for (PermissionInfo tmpInfo : mPermissionList) {
            //判断是否设置为不再提醒.
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, tmpInfo.permission)) {
                //设置不再提醒权限.
                notTemindPermissionList.add(tmpInfo.permission);
            } else {
                //禁止授权
                forbidPermissionList.add(tmpInfo.permission);
            }
        }
        //显示自定义授权弹框(优先禁止授权的权限, 其次再是不再提醒授权权限).
        if (!Constants.IS_AIGAO) {
            showPermissionDialog(activity, StringFormat.isEmpty(forbidPermissionList), !StringFormat.isEmpty(forbidPermissionList) ? forbidPermissionList : notTemindPermissionList);
        }
    }

    /**
     * 过滤已授权权限.
     *
     * @param isRemoveNotRequired 是否移除非必须权限(true:移除;false:不移除).
     * @return
     */
    private void filterAuthPermission(boolean isRemoveNotRequired) {
        if (!StringFormat.isEmpty(mPermissionList)) {
            PermissionInfo permissionInfo;
            for (int index = mPermissionList.size() - 1; index >= 0; index--) {
                try {
                    //获取权限对象.
                    permissionInfo = mPermissionList.get(index);
                    //判断是否已授权或移除非必须权限.
                    if (ActivityCompat.checkSelfPermission(BaseContext.getContext(), permissionInfo.permission) == PackageManager.PERMISSION_GRANTED || (isRemoveNotRequired && !permissionInfo.isRequired)) {
                        //移除已授权的权限.
                        mPermissionList.remove(index);
                    }
                } catch (Throwable throwable) {
                    Logger.e(TAG, "filterAuthPermission: {}, {}", isRemoveNotRequired, throwable);
                }
            }
        }
    }

    /**
     * 显示权限申请框.
     *
     * @param activity
     * @param alwaysRefuse   是否为不再提醒授权类型.
     * @param permissionList 授权列表
     */
    private void showPermissionDialog(final Activity activity, final boolean alwaysRefuse, final List<String> permissionList) {
        try {
            if (StringFormat.isEmpty(permissionList)) {
                //没有待授权权限.
                return;
            }
            //获取未授权权限.
            StringBuffer nameBuffer = new StringBuffer();
            if (mPermissionList != null) {
                for (PermissionInfo permissionInfo : mPermissionList) {
                    try {
                        if (!PhoneUtil.checkPermission(BaseContext.getContext(), permissionInfo.permission)) {
                            nameBuffer.append(permissionInfo.name).append("\n\n");
                        }
                    } catch (Throwable throwable) {
                        Logger.e(TAG, "showPermissionDialog: {}", throwable);
                    }
                }
            }
            //创建Builder.
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, com.zydm.base.R.style.Dialog));
            //获取要展示的View.
            View view = LayoutInflater.from(activity).inflate(R.layout.auth_tips_dialog, null);
            //获取申请权限TextView.
            mPermissionTextView = view.findViewById(R.id.permission_textview);
            //设置权限按钮.
            view.findViewById(R.id.to_set_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //去设置, 关闭Dialog.
                    cancelPermissionDialog();
                    if (alwaysRefuse) {
                        //已设置不再提醒, 进入系统授权页面.
                        SystemUtil.gotoPermissionPage(activity, REQUEST_CODE_PERMISSION);
                    } else {
                        String[] permissionArray = new String[permissionList.size()];
                        for (int index = 0; index < permissionList.size(); index++) {
                            permissionArray[index] = permissionList.get(index);
                        }
                        //发送授权请求(必须从后往前).
                        ActivityCompat.requestPermissions(activity, permissionArray, REQUEST_CODE_PERMISSION);
                    }
                }
            });
            //设置自定义View作为AlertDialog的子View.
            builder.setView(view);
            //创建Dialog.
            mPermissionDialog = builder.create();
            //设置点击Dialog以为区域外或返回键不消失.
            //mPermissionDialog.setCancelable(false);
            //设置点击Dialog以为区域外不消失, 点击返回键消失.
            mPermissionDialog.setCanceledOnTouchOutside(false);
            //设置背景透明，不然会出现白色直角问题
            Window window = mPermissionDialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置需要申请的权限.
            if (mPermissionTextView != null) {
                mPermissionTextView.setText(nameBuffer);
            }
            //展示Dialog.
            mPermissionDialog.show();
        } catch (Throwable throwable) {
            Logger.e(TAG, "showPermissionDialog: {}", throwable);
        }
    }

    /**
     * 关闭未授权成功提示框.
     */
    private void cancelPermissionDialog() {
        try {
            if (mPermissionDialog != null) {
                mPermissionDialog.cancel();
                mPermissionDialog = null;
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "cancelPermissionDialog: {}", throwable);
        }
    }

    /**
     * @param activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            //调用补充IMEI、IMSI等信息接口.
            MobileInfoPresenter.uploadSupplyMobileInfo();
            //授权完成,  启动定位.
            BDLocationMgr.startLocation();
            //由于不知道是否选择了允许所以需要再次判断
            boolean isSucc = false;
            if (Constants.IS_AIGAO) {
                isSucc = PermissionUtil.requestPermissions(activity); // 不会发起请求
            } else {
                isSucc = requestPermissions(activity);
            }
            if (isSucc) {
                //调用授权成功统计.
                FunctionStatsApi.authSucc();
                PushMgr.registerXimiPush(activity);
            }
        }
    }

    /**
     * 资源回收.
     */
    public static void onDestroy() {
        if (sInstance == null) {
            return;
        }
        //回收Dialog.
        sInstance.cancelPermissionDialog();
        sInstance = null;
    }

    /**
     * 权限信息
     */
    private class PermissionInfo {
        /**
         * 权限名称
         */
        String name;

        //String requestMsg;
        //String refuseMsg;

        /**
         * 是否为必须权限.
         */
        boolean isRequired;

        /**
         * 权限.
         */
        String permission;
    }
}
