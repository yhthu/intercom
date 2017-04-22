package com.jd.wly.intercom.input;

import com.jd.wly.intercom.job.JobHandler;
import com.jd.wly.intercom.util.AudioDataUtil;

/**
 * 音频编码，输入类型为short[]，输出为byte[]
 *
 * @author yanghao1
 */
public class Encoder extends JobHandler<short[], byte[]> {

    @Override
    public void handleRequest(short[] audioData) {
        byte[] encodedData = AudioDataUtil.raw2spx(audioData);
        getNextJobHandler().handleRequest(encodedData);
    }

    @Override
    public void free() {
        super.free();
        AudioDataUtil.free();
    }
}
