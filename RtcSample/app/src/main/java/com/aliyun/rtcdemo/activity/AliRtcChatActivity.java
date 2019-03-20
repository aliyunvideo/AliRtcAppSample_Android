package com.aliyun.rtcdemo.activity;

import android.graphics.PixelFormat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alivc.rtc.AliRtcAuthInfo;
import com.alivc.rtc.AliRtcEngine;
import com.alivc.rtc.AliRtcEngineEventListener;
import com.alivc.rtc.AliRtcEngineNotify;
import com.aliyun.rtcdemo.R;
import com.aliyun.rtcdemo.bean.RTCAuthInfo;

import org.webrtc.alirtcInterface.AliParticipantInfo;
import org.webrtc.alirtcInterface.AliSubscriberInfo;
import org.webrtc.sdk.SophonSurfaceView;

import static com.alivc.rtc.AliRtcEngine.AliRtcRenderMode.AliRtcRenderModeAuto;
import static com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth;
import static com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera;
import static com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo;
import static com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen;
import static com.aliyun.rtcdemo.utils.AliRtcConstants.SOPHON_RESULT_SIGNAL_HEARTBEAT_TIMEOUT;
import static com.aliyun.rtcdemo.utils.AliRtcConstants.SOPHON_SERVER_ERROR_POLLING;

/**
 * 音视频通话的activity
 *
 * @author 34738
 */
public class AliRtcChatActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 用户名
     */
    String mUsername;
    /**
     * 频道名
     */
    String mChannel;
    /**
     * rtcAuthInfo
     */
    RTCAuthInfo mRtcAuthInfo;
    private TextView mFinish;
    private TextView mJoinChannel;
    /**
     * 本地流播放view
     */
    private SophonSurfaceView mLocalView;
    /**
     * 远程流播放view
     */
    private SophonSurfaceView mRemoteView;
    /**
     * SDK提供的对音视频通话处理的引擎类
     */
    private AliRtcEngine mAliRtcEngine;
    private AliRtcEngine.AliVideoCanvas mAliVideoCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alirtc_activity_chat);

        getIntentData();

        initView();

        initValues();
    }

    private void getIntentData() {
        Bundle b = getIntent().getExtras();
        //用户名
        mUsername = b.getString("username");
        //频道
        mChannel = b.getString("channel");
        //rtcAuthInfo
        mRtcAuthInfo = (RTCAuthInfo)b.getSerializable("rtcAuthInfo");
    }

    private void initView() {
        mFinish = findViewById(R.id.tv_finish);
        mJoinChannel = findViewById(R.id.tv_join_channel);
        mLocalView = findViewById(R.id.sf_local_view);
        mRemoteView = findViewById(R.id.sf_remote_view);

        mFinish.setOnClickListener(this);
        mJoinChannel.setOnClickListener(this);
    }

    private void initValues() {
        //实例化,必须在主线程进行。
        mAliRtcEngine = AliRtcEngine.getInstance(getApplicationContext());
        //设置事件的回调监听
        mAliRtcEngine.setRtcEngineEventListener(mEventListener);
        //设置接受通知事件的回调
        mAliRtcEngine.setRtcEngineNotify(mEngineNotify);
        //开启预览
        startPreview();

    }

    private void startPreview() {
        mLocalView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mLocalView.setZOrderOnTop(false);
        mLocalView.setZOrderMediaOverlay(false);
        mAliVideoCanvas = new AliRtcEngine.AliVideoCanvas();
        mAliVideoCanvas.view = mLocalView;
        mAliVideoCanvas.renderMode = AliRtcRenderModeAuto;
        mAliRtcEngine.setLocalViewConfig(mAliVideoCanvas, AliRtcVideoTrackCamera);
        mAliRtcEngine.startPreview();
    }

    private void joinChannel() {
        AliRtcAuthInfo userInfo = new AliRtcAuthInfo();
        userInfo.setAppid(mRtcAuthInfo.data.appid);
        userInfo.setNonce(mRtcAuthInfo.data.nonce);
        userInfo.setTimestamp(mRtcAuthInfo.data.timestamp);
        userInfo.setUserId(mRtcAuthInfo.data.userid);
        userInfo.setGslb(mRtcAuthInfo.data.gslb);
        userInfo.setToken(mRtcAuthInfo.data.token);
        userInfo.setConferenceId(mChannel);
        mAliRtcEngine.joinChannel(userInfo, mUsername);

    }

    /**
     * 远端用户上线时更新
     *
     * @param uid
     * @param vt
     * @param at
     */
    private void updateDisplay(final String uid, final AliRtcEngine.AliRtcVideoTrack vt,
                               final AliRtcEngine.AliRtcAudioTrack at) {
        mRemoteView.setVisibility(View.VISIBLE);
        AliRtcEngine.AliVideoCanvas cameraCanvas = null;
        AliRtcEngine.AliVideoCanvas screenCanvas = null;

        if (vt == AliRtcVideoTrackNo) {
            cameraCanvas = null;
            screenCanvas = null;
        } else if (vt == AliRtcVideoTrackCamera) {
            if (cameraCanvas == null) {
                cameraCanvas = new AliRtcEngine.AliVideoCanvas();
                mRemoteView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                mRemoteView.setZOrderOnTop(true);
                mRemoteView.setZOrderMediaOverlay(true);
                cameraCanvas.view = mRemoteView;
                cameraCanvas.renderMode = AliRtcRenderModeAuto;
                mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcVideoTrackCamera);
            }
        } else if (vt == AliRtcVideoTrackScreen) {
            if (screenCanvas == null) {
                screenCanvas = new AliRtcEngine.AliVideoCanvas();
                mRemoteView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                mRemoteView.setZOrderOnTop(true);
                mRemoteView.setZOrderMediaOverlay(true);
                screenCanvas.view = mRemoteView;
                screenCanvas.renderMode = AliRtcRenderModeAuto;
                mAliRtcEngine.setRemoteViewConfig(screenCanvas, uid, AliRtcVideoTrackScreen);
            }
        } else if (vt == AliRtcVideoTrackBoth) {
            if (cameraCanvas == null) {
                cameraCanvas = new AliRtcEngine.AliVideoCanvas();
                mRemoteView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                mRemoteView.setZOrderOnTop(true);
                mRemoteView.setZOrderMediaOverlay(true);
                cameraCanvas.view = mRemoteView;
                cameraCanvas.renderMode = AliRtcRenderModeAuto;
                mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcVideoTrackScreen);
            }

            if (screenCanvas == null) {
                screenCanvas = new AliRtcEngine.AliVideoCanvas();
                mRemoteView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                mRemoteView.setZOrderOnTop(true);
                mRemoteView.setZOrderMediaOverlay(true);
                screenCanvas.view = mRemoteView;
                screenCanvas.renderMode = AliRtcRenderModeAuto;
                mAliRtcEngine.setRemoteViewConfig(screenCanvas, uid, AliRtcVideoTrackScreen);
            }
        }
    }

    /**
     * 特殊错误码回调的处理方法
     *
     * @param error 错误码
     */
    private void processOccurError(int error) {
        switch (error) {
            case SOPHON_SERVER_ERROR_POLLING:
            case SOPHON_RESULT_SIGNAL_HEARTBEAT_TIMEOUT:
                noSessionExit(error);
                break;
            default:
                break;
        }
    }

    /**
     * 错误处理
     *
     * @param error 错误码
     */
    private void noSessionExit(int error) {
        runOnUiThread(() -> new AlertDialog.Builder(AliRtcChatActivity.this)
            .setTitle("ErrorCode : " + error)
            .setMessage("网络超时，请退出房间")
            .setPositiveButton("确定", (dialog, which) -> {
                dialog.dismiss();
                onBackPressed();
            })
            .create()
            .show());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_finish:
                finish();
                break;
            case R.id.tv_join_channel:
                joinChannel();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAliRtcEngine != null) {
            mAliRtcEngine.leaveChannel();
            mAliRtcEngine = null;
        }
    }

    private AliRtcEngineEventListener mEventListener = new AliRtcEngineEventListener() {
        @Override
        public void onJoinChannelResult(int i) {
            mAliRtcEngine.publish();
        }

        @Override
        public void onLeaveChannelResult(int i) {

        }

        @Override
        public void onPublishResult(int i, String s) {

        }

        @Override
        public void onUnpublishResult(int i) {

        }

        @Override
        public void onSubscribeResult(String s, int i, AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack,
                                      AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack) {

        }

        @Override
        public void onUnsubscribeResult(int i, String s) {

        }

        @Override
        public void onNetworkQualityChanged(AliRtcEngine.AliRtcNetworkQuality aliRtcNetworkQuality) {

        }

        @Override
        public void onOccurWarning(int i) {

        }

        @Override
        public void onOccurError(int error) {
            //错误处理
            processOccurError(error);
        }
    };

    private AliRtcEngineNotify mEngineNotify = new AliRtcEngineNotify() {
        @Override
        public void onRemoteUserUnPublish(AliRtcEngine aliRtcEngine, String s) {

        }

        @Override
        public void onRemoteUserOnLineNotify(String s) {

        }

        @Override
        public void onRemoteUserOffLineNotify(String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //远端用户下线
                    mRemoteView.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        public void onRemoteTrackAvailableNotify(String s, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack,
                                                 AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //远端用户上线提醒
                    updateDisplay(s, aliRtcVideoTrack, aliRtcAudioTrack);
                }
            });
        }

        @Override
        public void onSubscribeChangedNotify(String s, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack,
                                             AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack) {

        }

        @Override
        public void onParticipantSubscribeNotify(AliSubscriberInfo[] aliSubscriberInfos, int i) {

        }

        @Override
        public void onFirstFramereceived(String s, String s1, String s2, int i) {

        }

        @Override
        public void onParticipantUnsubscribeNotify(AliParticipantInfo[] aliParticipantInfos, int i) {

        }

        @Override
        public void onBye(int i) {

        }
    };
}
