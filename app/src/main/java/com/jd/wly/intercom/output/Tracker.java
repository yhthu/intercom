package com.jd.wly.intercom.output;

import android.media.AudioTrack;
import android.os.Handler;

import com.jd.wly.intercom.data.AudioData;
import com.jd.wly.intercom.data.MessageQueue;
import com.jd.wly.intercom.input.Recorder;
import com.jd.wly.intercom.job.JobHandler;
import com.jd.wly.intercom.util.Constants;

/**
 * AudioTrack音频播放
 *
 * @author yanghao1
 */
public class Tracker extends JobHandler {

    private AudioTrack audioTrack;
    // 音频大小
    private int outAudioBufferSize;
    // 播放标志
    private boolean isPlaying = true;

    public Tracker(Handler handler) {
        super(handler);
        // 获取音频数据缓冲段大小
        outAudioBufferSize = AudioTrack.getMinBufferSize(
                Constants.sampleRateInHz, Constants.outputChannelConfig, Constants.audioFormat);
        // 初始化音频播放
        audioTrack = new AudioTrack(Constants.streamType,
                Constants.sampleRateInHz, Constants.outputChannelConfig, Constants.audioFormat,
                outAudioBufferSize, Constants.trackMode);
        audioTrack.play();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    @Override
    public void run() {
        AudioData audioData;
        while ((audioData = MessageQueue.getInstance(MessageQueue.TRACKER_DATA_QUEUE).take()) != null) {
            if (isPlaying()) {
                short[] bytesPkg = audioData.getRawData();
                try {
                    audioTrack.write(bytesPkg, 0, bytesPkg.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void free() {
        audioTrack.stop();
        audioTrack.release();
        audioTrack = null;
    }
}
