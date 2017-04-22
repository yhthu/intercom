package com.jd.wly.intercom.output;

import android.os.Handler;

/**
 * 音频接收、解码、播放线程
 *
 * @author yanghao1
 */
public class AudioOutput implements Runnable {

    private Handler handler;
    private Receiver receiver;
    private Decoder decoder;
    private Tracker tracker;

    public AudioOutput(Handler handler) {
        this.handler = handler;
        initJobHandler();
    }

    /**
     * 初始化接收、解码、播放，并指定关联
     */
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
