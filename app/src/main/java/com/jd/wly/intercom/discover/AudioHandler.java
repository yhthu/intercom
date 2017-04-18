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
    // Communication
    public static final int AUDIO_INPUT = 2;
    public static final int AUDIO_OUTPUT = 3;

    private WeakReference<AudioActivity> activityWeakReference;

    public AudioHandler(AudioActivity audioActivity) {
        activityWeakReference = new WeakReference<AudioActivity>(audioActivity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        AudioActivity activity = activityWeakReference.get();
        if (activity != null) {
            String content = (String) msg.obj;
            if (msg.what == DISCOVERING_SEND) {
//                activity.toast(content);
                activity.foundNewUser(IPUtil.getLocalIPAddress());
            } else if (msg.what == DISCOVERING_RECEIVE) {
                activity.foundNewUser(content);
            } else if (msg.what == AUDIO_OUTPUT) {
                activity.toast(content);
            }
        }
    }
}
