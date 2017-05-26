package com.jd.wly.intercom.output;

import android.os.Handler;

import com.jd.wly.intercom.data.AudioData;
import com.jd.wly.intercom.data.MessageQueue;
import com.jd.wly.intercom.job.JobHandler;
import com.jd.wly.intercom.util.AudioDataUtil;

/**
 * 音频解码
 *
 * @author yanghao1
 */
public class Decoder extends JobHandler {

    public Decoder(Handler handler) {
        super(handler);
    }

    @Override
    public void run() {
        AudioData audioData;
        // 当MessageQueue为空时，take方法阻塞
        while ((audioData = MessageQueue.getInstance(MessageQueue.DECODER_DATA_QUEUE).take()) != null) {
            audioData.setRawData(AudioDataUtil.spx2raw(audioData.getEncodedData()));
            MessageQueue.getInstance(MessageQueue.TRACKER_DATA_QUEUE).put(audioData);
        }
    }

    @Override
    public void free() {
        AudioDataUtil.free();
    }
}
