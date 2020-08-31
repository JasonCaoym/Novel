package com.duoyue.mianfei.xiaoshuo.read.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 序列化对象保存本地文件
 * @author wangtian
 * @date 2019/07/04
 */
public class ParcelableUtil {

    public static byte[] marshall(Parcelable parceable) {
        Parcel parcel = Parcel.obtain();
        parcel.setDataPosition(0);
        parceable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();

        Log.d("ParcelableTest", "bytes = " + String.valueOf(bytes) + "parcel" + parcel.toString());
        parcel.recycle();
        return bytes;
    }

    public static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return parcel;
    }

}
