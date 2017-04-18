package com.jd.wly.intercom.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.jd.wly.intercom.discover.DiscoverRequest;
import com.jd.wly.intercom.discover.DiscoverServer;
import com.jd.wly.intercom.input.AudioInput;
import com.jd.wly.intercom.output.AudioOutput;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class IntercomService extends Service {

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


    public IntercomService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 初始化录音、放音线程，并启动
     */
    private void initData() {
//        // 初始化探测线程
//        try {
//            discoverRequest = new DiscoverRequest(audioHandler);
//            discoverServer = new DiscoverServer(audioHandler);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // 启动探测局域网内其余用户的线程（每分钟扫描一次）
//        discoverService.scheduleAtFixedRate(discoverRequest, 0, 1, TimeUnit.MINUTES);
//        // 启动探测线程接收
//        inputService.execute(discoverServer);
//
//        // 初始化录音线程
//        audioInput = new AudioInput(audioHandler);
//
//        // 初始化并启动放音线程
//        audioOutput = new AudioOutput(audioHandler);
//        outputService.execute(audioOutput);
    }
}
