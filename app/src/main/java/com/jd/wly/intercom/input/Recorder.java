package com.jd.wly.intercom.input;

import android.media.AudioRecord;

import com.jd.wly.intercom.job.JobHandler;
import com.jd.wly.intercom.util.AECUtil;
import com.jd.wly.intercom.util.Constants;

/**
 * 音频录制数据格式ENCODING_PCM_16BIT，返回数据类型为short[]
 *
 * @author yanghao1
 */
public class Recorder extends JobHandler<short[], short[]> {

    private AudioRecord audioRecord;
    // 音频大小
    private int inAudioBufferSize;


    public Recorder() {
        // 获取音频数据缓冲段大小
        inAudioBufferSize = AudioRecord.getMinBufferSize(
                Constants.sampleRateInHz, Constants.inputChannelConfig, Constants.audioFormat);
        // 初始化音频录制
        audioRecord = new AudioRecord(Constants.audioSource,
                Constants.sampleRateInHz, Constants.inputChannelConfig, Constants.audioFormat, inAudioBufferSize);
        // 设置回音消除
        if (AECUtil.isDeviceSupport()) {
            AECUtil.initAEC(audioRecord.getAudioSessionId());
        }
    }

    @Override
    public void handleRequest(short[] audioData) {
        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
            audioRecord.startRecording();
        }
        // 实例化音频数据缓冲
        audioData = new short[inAudioBufferSize];
        audioRecord.read(audioData, 0, inAudioBufferSize);
        getNextJobHandler().handleRequest(audioData);
    }

    @Override
    public void free() {
        super.free();
        releaseAudioRecord();
        // 释放回音消除
        AECUtil.release();
    }

    /**
     * 释放音频录制资源
     */
    private void releaseAudioRecord() {
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
    }
}
