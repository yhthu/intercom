package com.jd.wly.intercom.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jd.wly.intercom.R;
import com.jd.wly.intercom.receive.ReceiveMsgHandler;
import com.jd.wly.intercom.receive.ServerSocket;
import com.jd.wly.intercom.send.ClientSocket;
import com.jd.wly.intercom.send.SendMsgHandler;
import com.jd.wly.intercom.util.IPUtil;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView ipTv;
    private TextView otherIpTv;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_dance).setOnClickListener(this);
        findViewById(R.id.btn_mobile_ip).setOnClickListener(this);
        ipTv = (TextView) findViewById(R.id.tv_ip);
        otherIpTv = (TextView) findViewById(R.id.other_ip);

        try {
            handler = new ReceiveMsgHandler(this);
            new ServerSocket(handler).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 发送广播
        handler = new SendMsgHandler(this);
        ClientSocket.getInstance().init(handler);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_mobile_ip:
                ipTv.setText(IPUtil.getLocalIPAddress());
                break;
            case R.id.btn_dance:
                String ip = IPUtil.getLocalIPAddress();
                if (TextUtils.isEmpty(ip)) {
                    return;
                }
                ClientSocket.getInstance().send("DISCOVER_REQUEST");
                break;

            default:
                break;
        }
    }

    public void setOtherIpTv(String msg) {
        String content;
        if (!(content = otherIpTv.getText().toString()).contains(msg)) {
            content = otherIpTv.getText().toString() + msg;
        }
        otherIpTv.setText(content);
    }
}
