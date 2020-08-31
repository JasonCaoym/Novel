package com.zydm.base.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import com.zydm.base.R;
import com.zydm.base.common.BaseApplication;

public class CustomToast {

    private boolean canceled = true;
        private Handler handler;
        private Toast toast;
        private TimeCount time;
        private TextView tvContent;

        public CustomToast(Context context) {
            this(context, new Handler());
        }

        public CustomToast(Context context, Handler handler) {
            this.handler = handler;

            if (toast == null) {
                toast = new Toast(context);
            }
            tvContent = new TextView(BaseApplication.context.globalContext);
            tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvContent.setTextColor(BaseApplication.context.globalContext.getResources().getColor(R.color.white));
            tvContent.setBackgroundResource(R.drawable.toast_custom_bg);
            int paddingStart = ViewUtils.dp2px(20);
            int paddingTop = ViewUtils.dp2px(7);
            tvContent.setPadding(paddingStart, paddingTop, paddingStart, paddingTop);

            toast.setGravity(Gravity.TOP, 0, ViewUtils.dp2px(50));
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(tvContent);
        }

        /**
         * @param text     要显示的内容
         * @param duration 显示的时间长
         *                 根据LENGTH_MAX进行判断
         *                 如果不匹配，进行系统显示
         *                 如果匹配，永久显示，直到调用hide()
         */
        public void show(String text, int duration) {
            time = new TimeCount(duration, 1000);
            tvContent.setText(text);
            if (canceled) {
                time.start();
                canceled = false;
                showUntilCancel();
            }
        }

        public void setGravity(int gravity, int xOffset, int yOffset) {
            toast.setGravity(gravity, xOffset, yOffset);
        }

        /**
         * 隐藏Toast
         */
        public void hide() {
            if (toast != null) {
                toast.cancel();
            }
            canceled = true;
        }

        private void showUntilCancel() {
            if (canceled) {
                return;
            }
            toast.show();
            handler.postDelayed(new Runnable() {
                public void run() {
                    showUntilCancel();
                }
            }, 3000);
        }

    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval); // 总时长,计时的时间间隔
        }

        @Override
        public void onFinish() { // 计时完毕时触发
            hide();
        }

        @Override
        public void onTick(long millisUntilFinished) { // 计时过程显示
        }
    }

}
