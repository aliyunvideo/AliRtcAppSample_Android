package com.aliyun.rtcdemo.network;

import com.aliyun.rtcdemo.network.api.AliRtcAuthService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求的帮助类
 */
public class AliRtcRetrofitHelper {

    private AliRtcRetrofitHelper() {

    }

    private static class HelpHolder {
        private static final Retrofit.Builder INSTANCE = new Retrofit.Builder();
    }

    public static Retrofit.Builder getRetrofitBuilder() {
        return HelpHolder.INSTANCE;
    }

    private static Retrofit getRetrofit(String url) {
        Retrofit retrofit = AliRtcRetrofitHelper.getRetrofitBuilder()
                .client(AliRtcOkHttpClientManager.getInstance().getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .build();

        return retrofit;
    }

    /**
     * 获取一个网络服务的实例，进行网络访问
     * @param url url
     * @return AuthService
     */
    public static AliRtcAuthService getAuthAPI(String url){
        return getRetrofit(url).create(AliRtcAuthService.class);
    }
}
