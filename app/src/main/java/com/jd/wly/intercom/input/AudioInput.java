package com.jd.wly.intercom.input;

import android.os.Handler;

/**
 * 音频录制、编码、发送线程
 *
 * @author yanghao1
 */
public class AudioInput implements Runnable {

    private Recorder recorder;
    private Encoder encoder;
    private Sender sender;
    private Handler handler;

    // 录制状态
    private boolean recording = false;

    public AudioInput(Handler handler) {
        this.handler = handler;
        initJobHandler();
    }

    /**
     * 初始化录制、编码、发送，并指定关联
     */
    private void initJobHandler() {
        recorder = new Recorder();
        encoder = new Encoder();
        sender = new Sender(handler);
        recorder.setNextJobHandler(encoder);
        encoder.setNextJobHandler(sender);
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    @Override
    public void run() {
        while (recording) {
            recorder.handleRequest(null);
        }
    }

    /**
     * 释放资源
     */
    public void free() {
        recorder.free();
        encoder.free();
        sender.free();
    }
}
