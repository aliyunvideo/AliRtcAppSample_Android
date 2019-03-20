package com.aliyun.rtcdemo.utils;

import com.alivc.rtc.AliRtcEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 常量类。包含网络请求，错误码
 * @author 34738
 */
public class AliRtcConstants {

    /**
     * 获取加入房间信息的url，用户自定义
     */
    public static final String GSLB_TEST = ;

    /**
     * 请求头携带的信息，可自定义
     */
    public static final String COMMON_UA_STR = "your UA";

    /**
     * 需要特殊处理的错误码，信令错误与心跳超时
     */
    public static final int SOPHON_SERVER_ERROR_POLLING = 0x02010105;
    public static final int SOPHON_RESULT_SIGNAL_HEARTBEAT_TIMEOUT = 0x0102020C;
}
