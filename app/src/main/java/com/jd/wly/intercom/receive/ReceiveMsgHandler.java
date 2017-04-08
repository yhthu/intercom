package com.jd.wly.intercom.receive;

import android.os.Handler;
import android.os.Message;

import com.jd.wly.intercom.activity.MainActivity;

/**
 * Created by yanghao1 on 2017/3/31.
 */

public class ReceiveMsgHandler extends Handler {
    public static final int RECEIVE_COMMOND = 1;
    private MainActivity activity;

    public ReceiveMsgHandler(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case RECEIVE_COMMOND:
                if (activity != null && msg.obj != null) {
                    activity.setOtherIpTv(msg.obj.toString());
                }
                break;
            default:
                break;
        }
    }
}
