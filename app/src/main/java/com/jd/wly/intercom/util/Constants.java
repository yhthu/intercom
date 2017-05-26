package com.jd.wly.intercom.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;

/**
 * Created by yanghao1 on 2017/4/14.
 */

public class Constants {

    // 组播端口号
    public static final int MULTI_BROADCAST_PORT = 9999;
    // 组播地址
    public static final String MULTI_BROADCAST_IP = "224.9.9.9";
    // 单播端口号
    public static final int UNICAST_PORT = 10000;
    // 采样频率
    public static final int sampleRateInHz = 44100;
    // 音频数据格式:PCM 16位每个样本，保证设备支持。
    public static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    // 音频获取源
    public static final int audioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
    // 输入单声道
    public static final int inputChannelConfig = AudioFormat.CHANNEL_IN_MONO;

    // 音频播放端
    public static final int streamType = AudioManager.STREAM_VOICE_CALL;
    // 输出单声道
    public static final int outputChannelConfig = AudioFormat.CHANNEL_OUT_MONO;
    // 音频输出模式
    public static final int trackMode = AudioTrack.MODE_STREAM;

}
