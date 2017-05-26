package com.jd.wly.intercom.input;

import android.os.Handler;

import com.jd.wly.intercom.data.AudioData;
import com.jd.wly.intercom.data.MessageQueue;
import com.jd.wly.intercom.job.JobHandler;
import com.jd.wly.intercom.util.AudioDataUtil;

/**
 * 音频编码
 *
 * @author yanghao1
 */
public class Encoder extends JobHandler {

    public Encoder(Handler handler) {
        super(handler);
    }

    @Override
    public void free() {
        AudioDataUtil.free();
    }

    @Override
    public void run() {
        AudioData data;
        // 在MessageQueue为空时，take方法阻塞
        while ((data = MessageQueue.getInstance(MessageQueue.ENCODER_DATA_QUEUE).take()) != null) {
            data.setEncodedData(AudioDataUtil.raw2spx(data.getRawData()));
            MessageQueue.getInstance(MessageQueue.SENDER_DATA_QUEUE).put(data);
        }
    }
}
