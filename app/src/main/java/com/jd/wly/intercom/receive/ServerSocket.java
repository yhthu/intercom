package com.jd.wly.intercom.receive;

import android.os.Handler;
import android.os.Message;

import com.jd.wly.intercom.util.IPUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerSocket extends Thread {
    private final int BROADCAST_PORT = 10000;
    private DatagramSocket datagramSocket;
    private Handler handler;

    public ServerSocket(Handler handler) throws IOException {
        // Keep a socket open to listen to all the UDP trafic that is destined for this port
        datagramSocket = new DatagramSocket(BROADCAST_PORT, InetAddress.getByName("0.0.0.0"));
        datagramSocket.setBroadcast(true);
        // handler
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte buf[] = new byte[1024];
                // 接收数据
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                datagramSocket.receive(packet);
                String content = new String(packet.getData()).trim();
                if (content.equals("DISCOVER_REQUEST") &&
                        !packet.getAddress().toString().equals("/" + IPUtil.getLocalIPAddress())) {
                    byte[] feedback = "DISCOVER_RESPONSE".getBytes();
                    // 发送数据
                    DatagramPacket sendPacket = new DatagramPacket(feedback, feedback.length,
                            packet.getAddress(), BROADCAST_PORT);
                    datagramSocket.send(sendPacket);
                    // 发送Handler消息
                    sendHandlerMessage(packet.getAddress().toString());
                } else if (content.equals("DISCOVER_RESPONSE") &&
                        !packet.getAddress().toString().equals("/" + IPUtil.getLocalIPAddress())) {
                    // 发送Handler消息
                    sendHandlerMessage(packet.getAddress().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送Handler消息
     *
     * @param content
     */
    private void sendHandlerMessage(String content) {
        Message msg = new Message();
        msg.what = ReceiveMsgHandler.RECEIVE_COMMOND;
        msg.obj = content;
        handler.sendMessage(msg);
    }
}
