package com.duoyue.app.userpoto;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import com.duoyue.mianfei.xiaoshuo.R;

/**
 * 展示用户协议Activity.
 */
public class UserPotoActivity extends Activity
{

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_poto);
        webView = findViewById(R.id.webview);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPotoActivity.this.finish();
            }
        });
        load();
    }


    public void load(){
        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
    }

}
