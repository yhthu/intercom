package com.jd.wly.intercom.output;

import android.os.Handler;

import com.jd.wly.intercom.input.Encoder;
import com.jd.wly.intercom.input.Recorder;
import com.jd.wly.intercom.input.Sender;

/**
 * Created by yanghao1 on 2017/4/11.
 */

public class AudioOutput implements Runnable{

    private Handler handler;
    private Receiver receiver;
    private Decoder decoder;
    private Tracker tracker;

    public AudioOutput(Handler handler) {
        this.handler = handler;
        initJobHandler();
    }

    private void initJobHandler() {
        receiver = new Receiver(handler);
        decoder = new Decoder();
        tracker = new Tracker();
        receiver.setNextJobHandler(decoder);
        decoder.setNextJobHandler(tracker);
    }

    @Override
    public void run() {
        while (true) {
            receiver.handleRequest(null);
        }
    }

    /**
     * 释放资源
     */
    public void free() {
        receiver.free();
        decoder.free();
        tracker.free();
    }
}
