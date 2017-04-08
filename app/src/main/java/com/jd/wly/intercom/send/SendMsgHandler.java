package com.jd.wly.intercom.send;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class SendMsgHandler extends Handler {
    public static final int STATUS = 2;
    private Activity activity;

    public SendMsgHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case STATUS:
                if (activity != null && msg.obj != null) {
                    Toast.makeText(activity, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
