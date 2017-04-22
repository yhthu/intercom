package com.jd.wly.intercom.util;

import com.jd.wly.intercom.audio.Speex;

/**
 * Created by yanghao1 on 2017/4/19.
 */

public class AudioDataUtil {

    /*The frame size in hardcoded for this sample code but it doesn't have to be*/
    private static int encFrameSize = 160;
    private static int decFrameSize = 160;
    private static int encodedFrameSize = 28;

    /**
     * 将raw原始音频文件编码为Speex格式
     *
     * @param audioData 原始音频数据
     * @return 编码后的数据
     */
    public static byte[] raw2spx(short[] audioData) {
        // 原始数据中包含的整数个encFrameSize
        int nSamples = audioData.length / encFrameSize;
        byte[] encodedData = new byte[((audioData.length - 1) / encFrameSize + 1) * encodedFrameSize];
        short[] rawByte;
        // 将原数据转换成spx压缩的文件
        byte[] encodingData = new byte[encFrameSize];
        int readTotal = 0;
        for (int i = 0; i < nSamples; i++) {
            rawByte = new short[encFrameSize];
            System.arraycopy(audioData, i * encFrameSize, rawByte, 0, encFrameSize);
            int encodeSize = Speex.getInstance().encode(rawByte, 0, encodingData, rawByte.length);
            System.arraycopy(encodingData, 0, encodedData, readTotal, encodeSize);
            readTotal += encodeSize;
        }
        rawByte = new short[encFrameSize];
        System.arraycopy(audioData, nSamples * encFrameSize, rawByte, 0, audioData.length - nSamples * encFrameSize);
        int encodeSize = Speex.getInstance().encode(rawByte, 0, encodingData, rawByte.length);
        System.arraycopy(encodingData, 0, encodedData, readTotal, encodeSize);
        return encodedData;
    }

    /**
     * 将Speex编码音频文件解码为raw音频格式
     *
     * @param encodedData 编码音频数据
     * @return 原始音频数据
     */
    public static short[] spx2raw(byte[] encodedData) {
        short[] shortRawData = new short[encodedData.length * decFrameSize / encodedFrameSize];
        int nSamples = encodedData.length / encodedFrameSize;
        byte[] encodedByte = new byte[encodedFrameSize];
        short[] decodingData = new short[decFrameSize];
        int decodeTotal = 0;
        for (int i = 0; i < nSamples; i++) {
            System.arraycopy(encodedData, i * encodedFrameSize, encodedByte, 0, encodedFrameSize);
            int decodeSize = Speex.getInstance().decode(encodedByte, decodingData, encodedByte.length);
            System.arraycopy(decodingData, 0, shortRawData, decodeTotal, decodeSize);
            decodeTotal += decodeSize;
        }
        return shortRawData;
    }

    /**
     * 释放音频编解码资源
     */
    public static void free() {
        Speex.getInstance().close();
    }
}
