package com.jd.wly.intercom.network;

import com.jd.wly.intercom.util.Constants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by yanghao1 on 2017/5/15.
 */

public class Unicast {

    byte[] receiveMsg = new byte[512];
    private DatagramPacket receivePacket;
    private DatagramSocket receiveSocket;

    private DatagramPacket sendPacket;
    private DatagramSocket sendSocket;

    private static final Unicast unicast = new Unicast();

    private Unicast() {
        try {
            // 初始化接收Socket
            receivePacket = new DatagramPacket(receiveMsg, receiveMsg.length);
            receiveSocket = new DatagramSocket(Constants.UNICAST_PORT);
            // 初始化发送Socket
            sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static Unicast getUnicast() {
        return unicast;
    }
}
