package com.aliyun.rtcdemo.base;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * create by xzy483800 0n 2019/2/27
 * presenter的基类
 * @author xzy
 */
public class AliRtcRxPresenter<T extends AliRtcBaseContract.BaseView> implements AliRtcBaseContract.BasePresenter<T>{
    protected T mView;
    protected CompositeSubscription mCompositeSubscription;

    @Override
    public void attachView(T view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
        unSubscribe();
    }

    protected void addSubscribe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    protected void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
