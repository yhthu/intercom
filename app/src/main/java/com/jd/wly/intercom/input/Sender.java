package com.jd.wly.intercom.input;

import android.os.Handler;

import com.jd.wly.intercom.data.AudioData;
import com.jd.wly.intercom.data.MessageQueue;
import com.jd.wly.intercom.job.JobHandler;
import com.jd.wly.intercom.util.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Socket发送
 *
 * @author yanghao1
 */
public class Sender extends JobHandler {

    // 组播Socket
    private MulticastSocket multicastSocket;
    // IPV4地址
    private InetAddress inetAddress;

    public Sender(Handler handler) {
        super(handler);
        initMulticastNetwork();
    }

    /**
     * 初始化组播网络
     */
    private void initMulticastNetwork() {
        try {
            inetAddress = InetAddress.getByName(Constants.MULTI_BROADCAST_IP);
            multicastSocket = new MulticastSocket();
            multicastSocket.setBroadcast(true);
            multicastSocket.setLoopbackMode(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        AudioData audioData;
        while ((audioData = MessageQueue.getInstance(MessageQueue.SENDER_DATA_QUEUE).take()) != null) {
            DatagramPacket datagramPacket = new DatagramPacket(
                    audioData.getEncodedData(), audioData.getEncodedData().length,
                    inetAddress, Constants.MULTI_BROADCAST_PORT);
            try {
                multicastSocket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void free() {
        super.free();
        multicastSocket.close();
    }
}
