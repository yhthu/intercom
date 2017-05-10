package com.jd.wly.intercom.discover;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jd.wly.intercom.R;
import com.jd.wly.intercom.app.App;
import com.jd.wly.intercom.multicast.Multicast;
import com.jd.wly.intercom.util.Command;
import com.jd.wly.intercom.util.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class DiscoverRequest implements Runnable {

    private Handler handler;

    public DiscoverRequest(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        byte[] data = Command.DISC_REQUEST.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(
                data, data.length, Multicast.getMulticast().getInetAddress(), Constants.MULTI_BROADCAST_PORT);
        try {
            Multicast.getMulticast().getMulticastSocket().send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMsg2MainThread();
    }

    /**
     * 发送消息到主线程
     */
    private void sendMsg2MainThread() {
        Message message = new Message();
        message.what = AudioHandler.DISCOVERING_SEND;
        handler.sendMessage(message);
    }
}
