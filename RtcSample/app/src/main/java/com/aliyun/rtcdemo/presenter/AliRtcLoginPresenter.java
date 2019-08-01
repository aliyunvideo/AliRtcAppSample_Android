package com.aliyun.rtcdemo.presenter;

import com.aliyun.rtcdemo.base.AliRtcRxPresenter;
import com.aliyun.rtcdemo.bean.RTCAuthInfo;
import com.aliyun.rtcdemo.contract.AliRtcLoginContract;
import com.aliyun.rtcdemo.network.AliRtcRetrofitHelper;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 登录activity的presenter类，进行网络和view的交互
 */
public class AliRtcLoginPresenter extends AliRtcRxPresenter<AliRtcLoginContract.view> implements
    AliRtcLoginContract.Present<AliRtcLoginContract.view>{

    @Override
    public void getAuthInfo(String name, String channelId, String url) {
        Subscription subscription = AliRtcRetrofitHelper.getAuthAPI(url).getRTCAuthInfo(name, channelId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(new Action0() {
                @Override
                public void call() {
                    mView.showProgressDialog(true);
                }
            })
            .doOnTerminate(new Action0() {
                @Override
                public void call() {
                    mView.showProgressDialog(false);
                }
            })
            .subscribe(new Action1<RTCAuthInfo>() {
                @Override
                public void call(RTCAuthInfo rtcAuthInfo) {
                    mView.showAuthInfo(rtcAuthInfo);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        addSubscribe(subscription);
    }
}
