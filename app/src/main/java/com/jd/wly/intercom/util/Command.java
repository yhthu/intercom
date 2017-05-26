package com.jd.wly.intercom.util;

/**
 * Created by yanghao1 on 2017/4/13.
 */

public class Command {

    // Service向Activity发送的跨进程指令
    public static final String DISC_REQUEST = "DISC_REQUEST";
    public static final String DISC_RESPONSE = "DISC_RESPONSE";
    public static final String DISC_LEAVE = "DISC_LEAVE";

    // Activity向Service发送的跨进程指令
    public static final String START_FOREGROUND_ACTION = "com.jd.wly.intercom.action.start";
    public static final String STOP_FOREGROUND_ACTION = "com.jd.wly.intercom.action.stop";
    // 前台Service
    public static final String MAIN_ACTION = "com.jd.wly.intercom.action.main";
    public static final int FOREGROUND_SERVICE = 101;
}
