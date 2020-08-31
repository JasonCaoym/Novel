package com.zydm.base.utils.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

public class RatioBlurTransformation extends BlurTransformation {

    private CropTransformation.CropType cropType;

    public RatioBlurTransformation(int radius, int sampling, CropTransformation.CropType cropType) {
        super(radius, sampling);
        this.cropType = cropType;
    }

    @Override
    protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        CropTransformation crop = new CropTransformation(outWidth, outHeight, cropType);
        toTransform = crop.transform(context, pool, toTransform, outWidth, outHeight);
        return super.transform(context, pool, toTransform, outWidth, outHeight);
    }
}
