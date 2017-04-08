package com.jd.wly.intercom.send;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.jd.wly.intercom.app.App;
import com.jd.wly.intercom.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class ClientSocket {
    private static ClientSocket mobileSocketClient = null;

    private final String BROADCAST_IP = "255.255.255.255";
    private final int BROADCAST_PORT = 10000;

    private InetAddress inetAddress;
    private Handler handler;

    private DatagramSocket datagramSocket;

    private ClientSocket() {
        try {
            inetAddress = InetAddress.getByName(BROADCAST_IP);
            datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(Handler handler) {
        this.handler = handler;
    }

    public static ClientSocket getInstance() {
        if (mobileSocketClient == null) {
            mobileSocketClient = new ClientSocket();
        }
        return mobileSocketClient;
    }

    public void send(final String content) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... paramVarArgs) {
                byte[] data = paramVarArgs[0].getBytes();
                DatagramPacket dataPacket = new DatagramPacket(data,
                        data.length, inetAddress, BROADCAST_PORT);
                try {
                    datagramSocket.send(dataPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                    return App.getInstance().getResources().getString(R.string.send_failed);
                }
                return App.getInstance().getResources().getString(R.string.send_success);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Message msg = new Message();
                msg.what = SendMsgHandler.STATUS;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        }.execute(content);

    }
}
