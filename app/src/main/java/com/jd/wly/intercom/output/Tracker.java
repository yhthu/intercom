package com.jd.wly.intercom.output;

import android.media.AudioTrack;

import com.jd.wly.intercom.job.JobHandler;
import com.jd.wly.intercom.util.Constants;

/**
 * AudioTrack音频播放
 *
 * @author yanghao1
 */
public class Tracker extends JobHandler<short[], short[]> {

    private AudioTrack audioTrack;
    // 音频大小
    private int outAudioBufferSize;

    public Tracker() {
        // 获取音频数据缓冲段大小
        outAudioBufferSize = AudioTrack.getMinBufferSize(
                Constants.sampleRateInHz, Constants.outputChannelConfig, Constants.audioFormat);
        // 初始化音频播放
        audioTrack = new AudioTrack(Constants.streamType,
                Constants.sampleRateInHz, Constants.outputChannelConfig, Constants.audioFormat,
                outAudioBufferSize, Constants.trackMode);
    }

    @Override
    public void handleRequest(short[] audioData) {
        short[] bytesPkg = audioData.clone();
        audioTrack.play();
        try {
            audioTrack.write(bytesPkg, 0, bytesPkg.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void free() {
        super.free();
        audioTrack.stop();
        audioTrack.release();
        audioTrack = null;
    }
}
