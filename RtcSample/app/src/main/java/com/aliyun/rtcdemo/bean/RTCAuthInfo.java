package com.aliyun.rtcdemo.bean;

import java.io.Serializable;

/**
 * 服务器返回的包含加入频道信息的业务类
 */
public class RTCAuthInfo implements Serializable {

    public int code;
    public RTCAuthInfo_Data data;

    public static class RTCAuthInfo_Data implements Serializable{
        public String appid;
        public String userid;
        public String nonce;
        public long timestamp;
        public String token;
        public RTCAuthInfo_Data_Turn turn;

        public static class RTCAuthInfo_Data_Turn implements Serializable{
            public String username;
            public String password;
        }

        public String[] gslb;
        public String key;
    }

    public int server;


}
