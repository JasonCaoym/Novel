package com.duoyue.mod.ad;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;
import com.zzdm.ad.R;

import java.util.Date;
import java.util.Locale;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class MainActivity extends Activity implements RewardVideoADListener, CompoundButton.OnCheckedChangeListener {


        private static final String TAG = "helloha";
        private RewardVideoAD rewardVideoAD;
        private CheckBox posIdCheckBox;
        private EditText posId;
        private boolean adLoaded;//广告加载成功标志
        private boolean videoCached;//视频素材文件下载完成标志

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            posId = findViewById(R.id.position_id);
            posIdCheckBox = findViewById(R.id.position_id_checkbox);
            posIdCheckBox.setOnCheckedChangeListener(this);
            posIdCheckBox.setChecked(true);
        }

        public void onClick(View view) {
            if (view.getId() == R.id.change_orientation_button) {
                int currentOrientation = getResources().getConfiguration().orientation;
                if (currentOrientation == ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (currentOrientation == ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            } else if (view.getId() == R.id.load_ad_button) {
                // 1. 初始化激励视频广告
                rewardVideoAD = new RewardVideoAD(this, "1101152570", getPosID(), this);
                adLoaded = false;
                videoCached = false;
                // 2. 加载激励视频广告
                rewardVideoAD.loadAD();
            } else if (view.getId() == R.id.show_ad_button) {
                    // 3. 展示激励视频广告
                    if (adLoaded && rewardVideoAD != null) {//广告展示检查1：广告成功加载，此处也可以使用videoCached来实现视频预加载完成后再展示激励视频广告的逻辑
                        if (!rewardVideoAD.hasShown()) {//广告展示检查2：当前广告数据还没有展示过
                            long delta = 1000;//建议给广告过期时间加个buffer，单位ms，这里demo采用1000ms的buffer
                            //广告展示检查3：展示广告前判断广告数据未过期
                            if (SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta)) {

                            } else {
                                Toast.makeText(this, "激励视频广告已过期，请再次请求广告后进行广告展示！", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(this, "此条广告已经展示过，请再次请求广告后进行广告展示！", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "成功加载广告后再进行广告展示！", Toast.LENGTH_LONG).show();
                    }
            }
        }

        private String getPosID() {
            return posId.getText().toString();
        }

        /**
         * 广告加载成功，可在此回调后进行广告展示
         **/
        @Override
        public void onADLoad() {
            adLoaded = true;
            String msg = "load ad success ! expireTime = " + new Date(System.currentTimeMillis() +
                    rewardVideoAD.getExpireTimestamp() - SystemClock.elapsedRealtime());
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

            rewardVideoAD.showAD();
        }

        /**
         * 视频素材缓存成功，可在此回调后进行广告展示
         */
        @Override
        public void onVideoCached() {
            videoCached = true;
            Log.i(TAG, "onVideoCached");
        }

        /**
         * 激励视频广告页面展示
         */
        @Override
        public void onADShow() {
            Log.i(TAG, "onADShow");
        }

        /**
         * 激励视频广告曝光
         */
        @Override
        public void onADExpose() {
            Log.i(TAG, "onADExpose");
        }

        /**
         * 激励视频触发激励（观看视频大于一定时长或者视频播放完毕）
         */
        @Override
        public void onReward() {
            Log.i(TAG, "onReward");
        }

        /**
         * 激励视频广告被点击
         */
        @Override
        public void onADClick() {
            Log.i(TAG, "onADClick");
        }

        /**
         * 激励视频播放完毕
         */
        @Override
        public void onVideoComplete() {
            Log.i(TAG, "onVideoComplete");
        }

        /**
         * 激励视频广告被关闭
         */
        @Override
        public void onADClose() {
            Log.i(TAG, "onADClose");
        }

        /**
         * 广告流程出错
         */
        @Override
        public void onError(AdError adError) {
            String msg = String.format(Locale.getDefault(), "onError, error code: %d, error msg: %s",
                    adError.getErrorCode(), adError.getErrorMsg());
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            posId.setText("5040942242835423");
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();

            Log.i(TAG, "onADClose");
        }
}
