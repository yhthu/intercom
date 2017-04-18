package com.jd.wly.intercom.input;

import android.os.Handler;

import com.jd.wly.intercom.input.Recorder;

/**
 * Created by yanghao1 on 2017/4/11.
 */
public class AudioInput implements Runnable {

    private Recorder recorder;
    private Encoder encoder;
    private Sender sender;
    private Handler handler;

    // 录制状态
    private boolean recording = false;
    // 音频数据
    private byte[] audioData = new byte[]{};

    public AudioInput(Handler handler) {
        this.handler = handler;
        initJobHandler();
    }

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
            recorder.handleRequest(audioData);
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
