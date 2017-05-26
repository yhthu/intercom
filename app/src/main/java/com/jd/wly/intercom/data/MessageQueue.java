package com.jd.wly.intercom.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by yanghao1 on 2017/4/25.
 */

public class MessageQueue {

    private static MessageQueue messageQueue1, messageQueue2, messageQueue3, messageQueue4;

    private BlockingQueue<AudioData> audioDataQueue = null;

    private MessageQueue() {
        audioDataQueue = new LinkedBlockingQueue<>();
    }

    @Retention(SOURCE)
    @IntDef({ENCODER_DATA_QUEUE, SENDER_DATA_QUEUE, DECODER_DATA_QUEUE, TRACKER_DATA_QUEUE})
    public @interface DataQueueType {
    }

    public static final int ENCODER_DATA_QUEUE = 0;
    public static final int SENDER_DATA_QUEUE = 1;
    public static final int DECODER_DATA_QUEUE = 2;
    public static final int TRACKER_DATA_QUEUE = 3;

    public static MessageQueue getInstance(@DataQueueType int type) {
        switch (type) {
            case ENCODER_DATA_QUEUE:
                if (messageQueue1 == null) {
                    messageQueue1 = new MessageQueue();
                }
                return messageQueue1;
            case SENDER_DATA_QUEUE:
                if (messageQueue2 == null) {
                    messageQueue2 = new MessageQueue();
                }
                return messageQueue2;
            case DECODER_DATA_QUEUE:
                if (messageQueue3 == null) {
                    messageQueue3 = new MessageQueue();
                }
                return messageQueue3;
            case TRACKER_DATA_QUEUE:
                if (messageQueue4 == null) {
                    messageQueue4 = new MessageQueue();
                }
                return messageQueue4;
            default:
                return new MessageQueue();
        }
    }

    public void put(AudioData audioData) {
        try {
            audioDataQueue.put(audioData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public AudioData take() {
        try {
            return audioDataQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getSize() {
        return audioDataQueue.size();
    }

    public synchronized void clear() {
        audioDataQueue.clear();
    }
}
