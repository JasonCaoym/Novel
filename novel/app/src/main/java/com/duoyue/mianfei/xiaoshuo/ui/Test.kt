package com.duoyue.mianfei.xiaoshuo.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.duoyue.lib.base.log.Logger

import android.content.pm.PackageManager.PERMISSION_GRANTED

/**
 * @author caoym
 * @data 2019/5/6  20:59
 */
class Test : Activity() {

    /**
     * 禁用且不再询问提示Dialog.
     */
    private var mDisableTipsDialog1: AlertDialog? = null

    /**
     * 禁用提示Dialog.
     */
    private var mDisableTipsDialog2: AlertDialog? = null

    private fun requestPermissions() {
        //必须权限.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PERMISSION_GRANTED) {
            //申请权限(读取手机信息、sdcard存储为必要权限)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1
            )
        }
        //非必须权限.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            //申请权限(位置为可选权限)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (i in permissions.indices) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    //选择了"始终允许"
                    Logger.i(TAG, "onRequestPermissionsResult: 成功: {}", permissions[i])
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        //用户选择了禁止不再询问
                        val builder = AlertDialog.Builder(this@Test)
                        builder.setTitle("permission")
                            .setMessage("点击允许才可以使用我们的app哦")
                            .setPositiveButton("去允许") { dialog, id ->
                                if (mDisableTipsDialog1 != null && mDisableTipsDialog1!!.isShowing) {
                                    mDisableTipsDialog1!!.dismiss()
                                }
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                //注意就是"package",不用改成自己的包名
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivityForResult(intent, NOT_NOTICE)
                            }
                        mDisableTipsDialog1 = builder.create()
                        mDisableTipsDialog1!!.setCanceledOnTouchOutside(false)
                        mDisableTipsDialog1!!.show()
                    } else {
                        //选择禁止
                        val builder = AlertDialog.Builder(this@Test)
                        builder.setTitle("permission")
                            .setMessage("点击允许才可以使用我们的app哦")
                            .setPositiveButton("去允许") { dialog, id ->
                                if (mDisableTipsDialog2 != null && mDisableTipsDialog2!!.isShowing) {
                                    mDisableTipsDialog2!!.dismiss()
                                }
                                ActivityCompat.requestPermissions(
                                    this@Test,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    1
                                )
                            }
                        mDisableTipsDialog2 = builder.create()
                        mDisableTipsDialog2!!.setCanceledOnTouchOutside(false)
                        mDisableTipsDialog2!!.show()
                    }

                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NOT_NOTICE) {
            //由于不知道是否选择了允许所以需要再次判断
            requestPermissions()
        }
    }

    companion object {

        /**
         * 如果勾选了不再询问
         */
        private val NOT_NOTICE = 2

        /**
         * 日志Tag
         */
        private val TAG = "Stats#Test"
    }
}
