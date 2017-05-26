package com.jd.wly.intercom.input;

import android.os.Handler;

import com.jd.wly.intercom.data.AudioData;
import com.jd.wly.intercom.data.MessageQueue;
import com.jd.wly.intercom.job.JobHandler;
import com.jd.wly.intercom.network.Multicast;
import com.jd.wly.intercom.util.Constants;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Socket发送
 *
 * @author yanghao1
 */
public class Sender extends JobHandler {

    public Sender(Handler handler) {
        super(handler);
    }

    @Override
    public void run() {
        AudioData audioData;
        while ((audioData = MessageQueue.getInstance(MessageQueue.SENDER_DATA_QUEUE).take()) != null) {
            DatagramPacket datagramPacket = new DatagramPacket(
                    audioData.getEncodedData(), audioData.getEncodedData().length,
                    Multicast.getMulticast().getInetAddress(), Constants.MULTI_BROADCAST_PORT);
            try {
                Multicast.getMulticast().getMulticastSocket().send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void free() {
        Multicast.getMulticast().free();
    }
}
