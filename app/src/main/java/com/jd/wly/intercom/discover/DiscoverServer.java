package com.jd.wly.intercom.discover;

import android.os.Handler;
import android.os.Message;

import com.jd.wly.intercom.util.Command;
import com.jd.wly.intercom.util.Constants;
import com.jd.wly.intercom.util.IPUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DiscoverServer implements Runnable {

    private DatagramSocket datagramSocket;
    private Handler handler;

    public DiscoverServer(Handler handler) throws IOException {
        // Keep a socket open to listen to all the UDP trafic that is destined for this port
        datagramSocket = new DatagramSocket(Constants.BROADCAST_PORT, InetAddress.getByName("0.0.0.0"));
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
                if (content.equals(Command.DISC_REQUEST) &&
                        !packet.getAddress().toString().equals("/" + IPUtil.getLocalIPAddress())) {
                    byte[] feedback = Command.DISC_RESPONSE.getBytes();
                    // 发送数据
                    DatagramPacket sendPacket = new DatagramPacket(feedback, feedback.length,
                            packet.getAddress(), Constants.BROADCAST_PORT);
                    datagramSocket.send(sendPacket);
                    // 发送Handler消息
                    sendMsg2MainThread(packet.getAddress().toString());
                } else if (content.equals(Command.DISC_RESPONSE) &&
                        !packet.getAddress().toString().equals("/" + IPUtil.getLocalIPAddress())) {
                    // 发送Handler消息
                    sendMsg2MainThread(packet.getAddress().toString());
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
    private void sendMsg2MainThread(String content) {
        Message msg = new Message();
        msg.what = AudioHandler.DISCOVERING_RECEIVE;
        msg.obj = content;
        handler.sendMessage(msg);
    }
}
