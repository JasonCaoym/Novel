package com.zydm.base.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Bitmap工具类
 * @author wangt
 * @date 2019/06/04
 */
public class BitmapUtils {

    private static final String TAG = "BitmapUtils";

    /**
     * Bitmap转换成byte[]并且进行压缩,压缩到不大于maxkb
     *
     * @param bitmap
     * @param maxKb
     * @return
     */
    public static byte[] bmpToByteArray(Bitmap bitmap, int maxKb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxKb * 1000 && options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        Log.i(TAG, "options:  " + options);

//        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);

        return output.toByteArray();
    }

}
