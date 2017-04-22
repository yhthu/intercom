package com.jd.wly.intercom.output;

import com.jd.wly.intercom.job.JobHandler;
import com.jd.wly.intercom.util.AudioDataUtil;

/**
 * 解码，输入类型是byte[]，输出类型是short[]
 *
 * @author yanghao1
 */
public class Decoder extends JobHandler<byte[], short[]> {

    @Override
    public void handleRequest(byte[] audioData) {
        short[] rawData = AudioDataUtil.spx2raw(audioData);
        getNextJobHandler().handleRequest(rawData);
    }

    @Override
    public void free() {
        super.free();
        AudioDataUtil.free();
    }
}
