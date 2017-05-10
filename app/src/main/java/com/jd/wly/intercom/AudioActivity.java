package com.jd.wly.intercom;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.jd.wly.intercom.discover.AudioHandler;
import com.jd.wly.intercom.discover.DiscoverRequest;
import com.jd.wly.intercom.input.Encoder;
import com.jd.wly.intercom.input.Recorder;
import com.jd.wly.intercom.input.Sender;
import com.jd.wly.intercom.output.Decoder;
import com.jd.wly.intercom.output.Receiver;
import com.jd.wly.intercom.output.Tracker;
import com.jd.wly.intercom.users.IntercomAdapter;
import com.jd.wly.intercom.users.IntercomUserBean;
import com.jd.wly.intercom.users.VerticalSpaceItemDecoration;
import com.jd.wly.intercom.util.IPUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AudioActivity extends Activity {

    private RecyclerView localNetworkUser;
    private TextView currentIp;

    private List<IntercomUserBean> userBeanList = new ArrayList<>();
    private IntercomAdapter intercomAdapter;

    private AudioHandler audioHandler = new AudioHandler(this);

    // 创建循环任务线程用于间隔的发送上线消息，获取局域网内其他的用户
    private ScheduledExecutorService discoverService = Executors.newScheduledThreadPool(1);
    // 创建7个线程的固定大小线程池，分别执行DiscoverServer，以及输入、输出音频
    private ExecutorService threadPool = Executors.newFixedThreadPool(6);

    // 音频输入
    private Recorder recorder;
    private Encoder encoder;
    private Sender sender;

    // 音频输出
    private Receiver receiver;
    private Decoder decoder;
    private Tracker tracker;

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
        // 添加自己
        addNewUser(new IntercomUserBean(IPUtil.getLocalIPAddress(), "我"));
        // 设置当前IP地址
        currentIp = (TextView) findViewById(R.id.activity_audio_current_ip);
        currentIp.setText(IPUtil.getLocalIPAddress());
    }

    /**
     * 初始化录音、放音线程，并启动
     */
    private void initData() {
        // 初始化探测线程
        DiscoverRequest discoverRequest = new DiscoverRequest(audioHandler);
        // 启动探测局域网内其余用户的线程（每分钟扫描一次）
        discoverService.scheduleAtFixedRate(discoverRequest, 0, 10, TimeUnit.SECONDS);

        // 初始化AudioManager配置
        initAudioManager();
        // 初始化JobHandler
        initJobHandler();
    }

    /**
     * 初始化AudioManager配置
     */
    private void initAudioManager() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);
    }

    /**
     * 初始化JobHandler
     */
    private void initJobHandler() {
        // 初始化音频输入节点
        recorder = new Recorder(audioHandler);
        encoder = new Encoder(audioHandler);
        sender = new Sender(audioHandler);
        // 初始化音频输出节点
        receiver = new Receiver(audioHandler);
        decoder = new Decoder(audioHandler);
        tracker = new Tracker(audioHandler);
        // 开启音频输入、输出
        threadPool.execute(encoder);
        threadPool.execute(sender);
        threadPool.execute(receiver);
        threadPool.execute(decoder);
        threadPool.execute(tracker);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_F2 ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            if (!recorder.isRecording()) {
                recorder.setRecording(true);
                tracker.setPlaying(false);
                threadPool.execute(recorder);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_F2 ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            if (recorder.isRecording()) {
                recorder.setRecording(false);
                tracker.setPlaying(true);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Process.killProcess(Process.myPid());
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
        IntercomUserBean userBean = new IntercomUserBean(ipAddress);
        if (!userBeanList.contains(userBean)) {
            addNewUser(userBean);
        }
    }

    /**
     * 更新自身IP
     */
    public void updateMyself() {
        currentIp.setText(IPUtil.getLocalIPAddress());
    }

    /**
     * 增加新的用户
     *
     * @param userBean 新用户
     */
    private void addNewUser(IntercomUserBean userBean) {
        userBeanList.add(0, userBean);
        intercomAdapter.notifyItemInserted(0);
        localNetworkUser.scrollToPosition(0);
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
        discoverService.shutdown();
        threadPool.shutdown();
        // 释放线程资源
        recorder.free();
        encoder.free();
        sender.free();
        receiver.free();
        decoder.free();
        tracker.free();
    }
}
