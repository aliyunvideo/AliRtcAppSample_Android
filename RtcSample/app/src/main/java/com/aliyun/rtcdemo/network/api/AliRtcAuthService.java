package com.aliyun.rtcdemo.network.api;

import com.aliyun.rtcdemo.bean.RTCAuthInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 请求网络的服务
 */
public interface AliRtcAuthService {

    /**
     * 获取频道信息
     * @param userName 用户名
     * @param roomNum 频道号
     * @return
     */
    @GET("app/v1/login?passwd=12345678")
    Observable<RTCAuthInfo> getRTCAuthInfo(@Query("user") String userName, @Query("room") String roomNum);
}
