package com.jd.wly.intercom.output;

import android.os.Handler;

import com.jd.wly.intercom.data.AudioData;
import com.jd.wly.intercom.data.MessageQueue;
import com.jd.wly.intercom.job.JobHandler;
import com.jd.wly.intercom.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by yanghao1 on 2017/4/12.
 */

public class Receiver extends JobHandler {

    // 组播Socket
    private MulticastSocket multicastSocket;
    // IPV4地址
    private InetAddress group;

    public Receiver(Handler handler) {
        super(handler);
        initMulticastNetwork();
    }

    private void initMulticastNetwork() {
        try {
            group = InetAddress.getByName(Constants.MULTI_BROADCAST_IP);
            multicastSocket = new MulticastSocket(Constants.MULTI_BROADCAST_PORT);
            multicastSocket.joinGroup(group);
            multicastSocket.setLoopbackMode(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            byte[] decodedData = new byte[336];
            DatagramPacket datagramPacket = new DatagramPacket(decodedData, decodedData.length);
            try {
                multicastSocket.receive(datagramPacket);
                AudioData audioData = new AudioData(decodedData);
                MessageQueue.getInstance(MessageQueue.DECODER_DATA_QUEUE).put(audioData);
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
