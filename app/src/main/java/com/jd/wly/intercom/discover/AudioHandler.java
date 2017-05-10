package com.jd.wly.intercom.discover;

import android.os.Handler;
import android.os.Message;

import com.jd.wly.intercom.AudioActivity;
import com.jd.wly.intercom.util.IPUtil;

import java.lang.ref.WeakReference;

/**
 * Created by yanghao1 on 2017/4/12.
 */

public class AudioHandler extends Handler {

    // Peer Discovering
    public static final int DISCOVERING_SEND = 0;
    public static final int DISCOVERING_RECEIVE = 1;

    private WeakReference<AudioActivity> activityWeakReference;

    public AudioHandler(AudioActivity audioActivity) {
        activityWeakReference = new WeakReference<AudioActivity>(audioActivity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        AudioActivity activity = activityWeakReference.get();
        if (activity != null) {
            if (msg.what == DISCOVERING_SEND) {
                activity.updateMyself();
            } else if (msg.what == DISCOVERING_RECEIVE) {
                activity.foundNewUser((String) msg.obj);
            }
        }
    }
}
