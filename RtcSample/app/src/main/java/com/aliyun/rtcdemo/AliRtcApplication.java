package com.aliyun.rtcdemo;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * 程序入口
 */
public class AliRtcApplication extends Application {

    private static AliRtcApplication sInstance;

    protected RefWatcher refWatcher;

    public static AliRtcApplication getInstance(){
        return sInstance;
    }

    public static RefWatcher getRefWatcher(Context context){
        AliRtcApplication application = (AliRtcApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
        // Normal app init code...
    }
}
