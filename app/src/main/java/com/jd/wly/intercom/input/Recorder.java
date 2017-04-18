package com.jd.wly.intercom.input;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.jd.wly.intercom.job.JobHandler;
import com.jd.wly.intercom.util.Constants;

/**
 * Created by yanghao1 on 2017/4/12.
 */

public class Recorder extends JobHandler {

    private static AudioRecord audioRecord;

    // 音频大小
    private int inAudioBufferSize;

    public Recorder() {
        // 获取音频数据缓冲段大小
        inAudioBufferSize = AudioRecord.getMinBufferSize(
                Constants.sampleRateInHz, Constants.inputChannelConfig, Constants.audioFormat);
        // 初始化音频录制
        audioRecord = new AudioRecord(Constants.audioSource,
                Constants.sampleRateInHz, Constants.inputChannelConfig, Constants.audioFormat, inAudioBufferSize);
    }

    @Override
    public void handleRequest(byte[] audioData) {
        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
            audioRecord.startRecording();
        }
        // 实例化音频数据缓冲
        audioData = new byte[inAudioBufferSize];
        audioRecord.read(audioData, 0, inAudioBufferSize);
        getNextJobHandler().handleRequest(audioData);
    }

    @Override
    public void free() {
        super.free();
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
    }
}
