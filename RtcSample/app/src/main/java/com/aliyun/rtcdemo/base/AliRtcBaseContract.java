package com.aliyun.rtcdemo.base;

/**
 *  管理view与presenter交互的基类
 */
public interface AliRtcBaseContract {

    /**
     * presenter层的积累
     * @param <T>
     */
    interface BasePresenter<T>{
        /**
         * 将presenter与view绑定
         * @param view
         */
        void attachView(T view);

        /**
         * 解绑presenter与view
         * @note 在activity生命周期方法onDestroy中调用
         */
        void detachView();
    }

    /**
     * view层的基类
     */
    interface BaseView{

    }
}
