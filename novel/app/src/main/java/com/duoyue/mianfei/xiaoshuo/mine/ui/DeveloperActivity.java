package com.duoyue.mianfei.xiaoshuo.mine.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.data.net.DomainConfig;
import com.zydm.base.data.net.DomainType;
import com.zydm.base.tools.PhoneStatusManager;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.StringUtils;
import com.zydm.base.utils.SysUtils;
import com.zydm.base.utils.ViewUtils;
import com.zydm.base.widgets.MTDialog;

public class DeveloperActivity extends BaseActivity {

    private EditText mApiEdit;
    private EditText mSearchEdit;
    private EditText mStatisticsEdit;
    private TextView mChannelTV;
    private TextView mSignatureText;
    private EditText mVersionNameEdit;
    private EditText mClientEdit;
    private EditText mImeiEdit;

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.domain_switch_btn:
                    resetEditText(!isChecked);
                    break;
            }
        }
    };
    private EditText mPkgEdit;
    private EditText mH5Edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.developer_activity);
        findViews();
    }

    private void findViews() {
        mApiEdit = findView(R.id.api_edit);
        mSearchEdit = findView(R.id.search_edit);
        mH5Edit = findView(R.id.h5_edit);
        mStatisticsEdit = findView(R.id.statistics_edit);
        mChannelTV = findView(R.id.channel_tv);
        mSignatureText = findView(R.id.signature_text);
        mVersionNameEdit = findView(R.id.version_name_tv);
        mVersionNameEdit.setText(PhoneStatusManager.getInstance().getAppVersionName());
        mImeiEdit = findView(R.id.imei_tv);
        mImeiEdit.setText(PhoneStatusManager.getInstance().getImei());
        mPkgEdit = findView(R.id.pkg_tv);
        mPkgEdit.setText(PhoneStatusManager.getInstance().getPackageNameOnlyForDeveloperTest());
        mClientEdit = findView(R.id.client_tv);
        initViewUriEdit();
        Switch domainSwitch = findView(R.id.domain_switch_btn);
        findViewSetOnClick(R.id.clean_first_pay_sign);

        domainSwitch.setChecked(!BaseApplication.context.isTestEnv());
        domainSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        initEditText();
    }

    private void initViewUriEdit() {
        final EditText text = findView(R.id.view_uri);
        text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    try {
                        String urlSt = text.getText().toString();
                        if (StringUtils.isBlank(urlSt)) {
                            return;
                        }
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri content_url = Uri.parse(urlSt);
                        intent.setData(content_url);
                        DeveloperActivity.this.startActivity(intent);
                    } catch (Throwable e) {
                    }
                }
            }
        });
    }

    private void initEditText() {
        mApiEdit.setText(DomainConfig.INSTANCE.getDomainName(DomainType.DEFAULT));
        mSearchEdit.setText(DomainConfig.INSTANCE.getDomainName(DomainType.SEARCH));
        mStatisticsEdit.setText(DomainConfig.INSTANCE.getDomainName(DomainType.STATISTICS));
        mH5Edit.setText(DomainConfig.INSTANCE.getDomainName(DomainType.H5_PAGE));
        mChannelTV.setText(PhoneStatusManager.getInstance().getAppChannel());
        mSignatureText.setText(getString(R.string.signature_text, String.valueOf(SysUtils.isSignatureOfficial())));

        mChannelTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final MTDialog dialog = new MTDialog(DeveloperActivity.this);
                final String[] channels = ViewUtils.getResources().getStringArray(R.array.all_channel);
                dialog.setSingleChoiceItems(channels, 0, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mChannelTV.setText(channels[position]);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public String getCurrPageId() {
        return PageNameConstants.DEVELOP;
    }

    private void resetEditText(boolean isTextEnv) {
        mApiEdit.setText(DomainConfig.INSTANCE.getStaticDomain(DomainType.DEFAULT, isTextEnv));
        mSearchEdit.setText(DomainConfig.INSTANCE.getStaticDomain(DomainType.SEARCH, isTextEnv));
        mStatisticsEdit.setText(DomainConfig.INSTANCE.getStaticDomain(DomainType.STATISTICS, isTextEnv));
        mH5Edit.setText(DomainConfig.INSTANCE.getStaticDomain(DomainType.H5_PAGE, isTextEnv));
    }

    @Override
    protected void onDestroy() {
        String api = mApiEdit.getText().toString().trim();
        String search = mSearchEdit.getText().toString().trim();
        String statistics = mStatisticsEdit.getText().toString().trim();
        String h5 = mH5Edit.getText().toString().trim();
        DomainConfig.INSTANCE.setDomains(api, search, statistics, h5);
        PhoneStatusManager.getInstance().updateChannelForTest(mChannelTV.getText().toString().trim());
        PhoneStatusManager.getInstance().updatePkgForTest(mPkgEdit.getText().toString().trim());
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.clean_first_pay_sign:
                break;
            case R.id.subscription_load:
                break;
            case R.id.submit_visit:
                submitVisit();
                break;
        }
    }

    private void googlePay() {
        try {
            Intent intent = new Intent(this, Class.forName("com.motong.cm.google.pay.GooglePayActivity"));
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void submitVisit() {
        PhoneStatusManager.getInstance().updateChannelForTest(mChannelTV.getText().toString().trim());
        PhoneStatusManager.getInstance().updatePkgForTest(mPkgEdit.getText().toString().trim());
        PhoneStatusManager.CLIENT_TYPE = mClientEdit.getText().toString().trim();
        PhoneStatusManager.getInstance().mAppVer = mVersionNameEdit.getText().toString().trim();
        PhoneStatusManager.getInstance().mImei = mImeiEdit.getText().toString().trim();
    }
}
