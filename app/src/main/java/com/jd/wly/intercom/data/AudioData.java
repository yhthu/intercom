package com.jd.wly.intercom.data;

/**
 * Created by yanghao1 on 2017/4/25.
 */

public class AudioData {

    /**
     * 原始数据
     */
    private short[] rawData;

    /**
     * 加密数据
     */
    private byte[] encodedData;

    public AudioData() {
    }

    public AudioData(short[] rawData) {
        this.rawData = rawData;
    }

    public AudioData(byte[] encodedData) {
        this.encodedData = encodedData;
    }

    public short[] getRawData() {
        return rawData;
    }

    public void setRawData(short[] rawData) {
        this.rawData = rawData;
    }

    public byte[] getEncodedData() {
        return encodedData;
    }

    public void setEncodedData(byte[] encodedData) {
        this.encodedData = encodedData;
    }
}
