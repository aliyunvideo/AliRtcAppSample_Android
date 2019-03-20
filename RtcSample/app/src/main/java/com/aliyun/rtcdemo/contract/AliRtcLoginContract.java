package com.aliyun.rtcdemo.contract;

import com.aliyun.rtcdemo.base.AliRtcBaseContract;
import com.aliyun.rtcdemo.bean.RTCAuthInfo;

/**
 * create by xzy483800 0n 2019/2/27
 * view与presenter交互的contract管理类
 * @author xzy
 */
public interface AliRtcLoginContract {

    interface view extends AliRtcBaseContract.BaseView {
        /**
         * 调用View层
         *
         * @param rtcAuthInfo
         */
        void showAuthInfo(RTCAuthInfo rtcAuthInfo);

        /**
         * 网络访问过程中的进度条
         * @param isShow 是否显示
         */
        void showProgressDialog(boolean isShow);
    }

    interface Present<T> extends AliRtcBaseContract.BasePresenter<T> {
        /**
         * View层调用
         * @param name 用户名
         * @param channelId 频道id
         * @param url url
         */
        void getAuthInfo(String name, String channelId, String url);
    }
}
