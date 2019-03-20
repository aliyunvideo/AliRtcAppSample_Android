package com.aliyun.rtcdemo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alivc.rtc.AliRtcEngine;
import com.aliyun.rtcdemo.R;
import com.aliyun.rtcdemo.bean.RTCAuthInfo;
import com.aliyun.rtcdemo.contract.AliRtcLoginContract;
import com.aliyun.rtcdemo.presenter.AliRtcLoginPresenter;
import com.aliyun.rtcdemo.utils.AliRtcConstants;
import com.aliyun.rtcdemo.utils.PermissionUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * create by xzy483800 0n 2019/2/27
 * 登录activity
 * @author xzy
 */
public class AliRtcLoginActivity extends AppCompatActivity implements View.OnClickListener, AliRtcLoginContract.view {

    private EditText mEtChannelId;
    private AliRtcEngine mAliRtcEngine;
    private TextView mSdkVersion;
    private Button mLoginChannel;
    private ProgressDialog mProgressDialog;
    private AliRtcLoginPresenter mLoginPresenter;
    private String mUserName;
    private String mChannelId;
    /**
     * 频道id最小长度
     */
    private static final int CHANNELID_MIN_SIZE = 3;
    /**
     * 频道id最大长度
     */
    private static final int CHANNELID_MAX_SIZE = 12;
    /**
     * 防止抖动
     */
    public static final int MIN_CLICK_DELAY_TIME = 1500;
    private long mLastClickTime = 0;

    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alirtc_activity_login);

        setUpSplash();

        initEngine();

        initView();

        initData();
    }

    private void initEngine() {
        mAliRtcEngine = AliRtcEngine.getInstance(this);
        mLoginPresenter = new AliRtcLoginPresenter();
        mLoginPresenter.attachView(this);
    }

    private void initView() {
        mEtChannelId = findViewById(R.id.et_channel);
        mLoginChannel = findViewById(R.id.bt_AuthInfo);
        mSdkVersion = findViewById(R.id.tv_sdk_version);

        mLoginChannel.setOnClickListener(this);
    }

    private void initData() {
        if (null != mAliRtcEngine) {
            mSdkVersion.setText(mAliRtcEngine.getSdkVersion());
        }
    }

    /**
     * 跳转频道activity
     */
    private void doCreateChannel() {
        mChannelId = mEtChannelId.getText().toString().trim();
        if (mChannelId.isEmpty()) {
            Toast.makeText(AliRtcLoginActivity.this, getString(R.string.alirtc_attention), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mChannelId.length() < CHANNELID_MIN_SIZE || mChannelId.length() > CHANNELID_MAX_SIZE) {
            Toast.makeText(AliRtcLoginActivity.this, getString(R.string.alirtc_channel_error), Toast.LENGTH_SHORT).show();
            return;
        }

        //用户名
        mUserName = randomName();

        mLoginPresenter.getAuthInfo(mUserName, mChannelId,AliRtcConstants.GSLB_TEST);
    }

    /**
     * 随机生成用户名
     * @return
     */
    private String randomName() {
        Random rd = new Random();
        String str = "";
        for (int i = 0; i < 5; i++) {

            // 你想生成几个字符
            str = str + (char) (Math.random() * 26 + 'a');
        }
        return str;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_AuthInfo:
                long currentTime = System.currentTimeMillis();
                if (currentTime - mLastClickTime > MIN_CLICK_DELAY_TIME) {
                    mLastClickTime = currentTime;
                    doCreateChannel();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 网络获取加入频道信息
     * @param rtcAuthInfo
     */
    @Override
    public void showAuthInfo(RTCAuthInfo rtcAuthInfo) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("nova://chat"));
        Bundle b = new Bundle();
        //用户名
        b.putString("username", mUserName);
        //频道号
        String channel = mEtChannelId.getText().toString();
        b.putString("channel", channel);
        b.putSerializable("rtcAuthInfo", rtcAuthInfo);
        intent.putExtras(b);
        startActivity(intent);
    }

    /**
     * 进入房间过程中的加载动画
     * @param isShow
     */
    @Override
    public void showProgressDialog(boolean isShow) {
        if (isShow && mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        } else if (isShow && mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("登陆中...");
            mProgressDialog.show();
        } else if (!isShow && mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出时释放AliRtcEngine
        if (mAliRtcEngine != null) {
            mAliRtcEngine.leaveChannel();
            mAliRtcEngine = null;
        }
        //解绑
        mLoginPresenter.detachView();
    }

    /**
     * 请求权限
     */
    private void setUpSplash() {
        Subscription splash = Observable.timer(2000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(aLong -> requestPermission());
        mCompositeSubscription.add(splash);
    }

    private PermissionUtils.PermissionGrant mGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {

        }

        @Override
        public void onPermissionCancel() {
            Toast.makeText(AliRtcLoginActivity.this, getString(R.string.alirtc_permission), Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private void requestPermission(){
        PermissionUtils.requestMultiPermissions(this,
            new String[]{
                PermissionUtils.PERMISSION_CAMERA,
                PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.PERMISSION_RECORD_AUDIO,
                PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, mGrant);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PermissionUtils.CODE_MULTI_PERMISSION){
            PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mGrant);
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PermissionUtils.REQUEST_CODE_SETTING){
            new Handler().postDelayed(this::requestPermission, 500);

        }

    }
}
