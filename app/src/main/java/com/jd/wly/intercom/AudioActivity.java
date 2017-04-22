package com.jd.wly.intercom;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.jd.wly.intercom.discover.AudioHandler;
import com.jd.wly.intercom.discover.DiscoverRequest;
import com.jd.wly.intercom.discover.DiscoverServer;
import com.jd.wly.intercom.input.AudioInput;
import com.jd.wly.intercom.output.AudioOutput;
import com.jd.wly.intercom.users.IntercomAdapter;
import com.jd.wly.intercom.users.IntercomUserBean;
import com.jd.wly.intercom.users.VerticalSpaceItemDecoration;
import com.jd.wly.intercom.util.IPUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class AudioActivity extends Activity {

    private RecyclerView localNetworkUser;
    private TextView currentIp;

    private List<IntercomUserBean> userBeanList = new ArrayList<>();
    private IntercomAdapter intercomAdapter;

    private AudioHandler audioHandler = new AudioHandler(this);

    private AudioInput audioInput;
    private AudioOutput audioOutput;

    // 创建缓冲线程池用于录音和接收用户上线消息（录音线程可能长时间不用，应该让其超时回收）
    private ExecutorService inputService = Executors.newCachedThreadPool();

    // 创建循环任务线程用于间隔的发送上线消息，获取局域网内其他的用户
    private ScheduledExecutorService discoverService = Executors.newScheduledThreadPool(1);

    // 设置音频播放线程为守护线程
    private ExecutorService outputService = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        }
    });

    // 探测局域网内其他用户的线程
    private DiscoverRequest discoverRequest;
    private DiscoverServer discoverServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        initView();
        initData();
    }

    private void initView() {
        // 设置用户列表
        localNetworkUser = (RecyclerView) findViewById(R.id.activity_audio_local_network_user_rv);
        localNetworkUser.setLayoutManager(new LinearLayoutManager(this));
        localNetworkUser.addItemDecoration(new VerticalSpaceItemDecoration(10));
        localNetworkUser.setItemAnimator(new DefaultItemAnimator());
        intercomAdapter = new IntercomAdapter(userBeanList);
        localNetworkUser.setAdapter(intercomAdapter);
        // 设置当前IP地址
        currentIp = (TextView) findViewById(R.id.activity_audio_current_ip);
        String ip = "当前IP地址为：" + IPUtil.getLocalIPAddress();
        currentIp.setText(ip);
    }

    /**
     * 初始化录音、放音线程，并启动
     */
    private void initData() {
        // 初始化探测线程
        try {
            discoverRequest = new DiscoverRequest(audioHandler);
            discoverServer = new DiscoverServer(audioHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 启动探测局域网内其余用户的线程（每分钟扫描一次）
        discoverService.scheduleAtFixedRate(discoverRequest, 0, 1, TimeUnit.MINUTES);
        // 启动探测线程接收
        inputService.execute(discoverServer);

        // 初始化录音线程
        audioInput = new AudioInput(audioHandler);

        // 初始化并启动放音线程
        audioOutput = new AudioOutput(audioHandler);
        outputService.execute(audioOutput);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_F2 ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                && !audioInput.isRecording()) {
            startRec();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_F2 ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                && audioInput.isRecording()) {
            stopRec();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 开始Recorder
     */
    private void startRec() {
        audioInput.setRecording(true);
        inputService.execute(audioInput);
    }

    /**
     * 关闭Recorder
     */
    private void stopRec() {
        audioInput.setRecording(false);
    }

    /**
     * 在UI线程中发送Toast提示
     *
     * @param msg
     */
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 发现新的用户地址
     *
     * @param ipAddress
     */
    public void foundNewUser(String ipAddress) {
        IntercomUserBean userBean;
        if (ipAddress.contains(IPUtil.getLocalIPAddress())) {
            userBean = new IntercomUserBean(ipAddress, "我");
        } else {
            userBean = new IntercomUserBean(ipAddress);
        }
        if (!userBeanList.contains(userBean)) {
            userBeanList.add(0, userBean);
            intercomAdapter.notifyItemInserted(0);
            localNetworkUser.scrollToPosition(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        free();
    }

    /**
     * 释放系统资源
     */
    private void free() {
        // 释放线程池
        inputService.shutdown();
        discoverService.shutdown();
        outputService.shutdown();
        // 释放线程资源
        audioInput.free();
        audioOutput.free();
    }
}
