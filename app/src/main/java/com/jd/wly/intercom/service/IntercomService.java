package com.jd.wly.intercom.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jd.wly.intercom.AudioActivity;
import com.jd.wly.intercom.R;
import com.jd.wly.intercom.discover.SignInAndOutReq;
import com.jd.wly.intercom.input.Encoder;
import com.jd.wly.intercom.input.Recorder;
import com.jd.wly.intercom.input.Sender;
import com.jd.wly.intercom.output.Decoder;
import com.jd.wly.intercom.output.Receiver;
import com.jd.wly.intercom.output.Tracker;
import com.jd.wly.intercom.util.Command;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IntercomService extends Service {

    // 创建循环任务线程用于间隔的发送上线消息，获取局域网内其他的用户
    private ScheduledExecutorService discoverService = Executors.newScheduledThreadPool(1);
    // 创建7个线程的固定大小线程池，分别执行DiscoverServer，以及输入、输出音频
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    // 加入、退出组播组消息
    private SignInAndOutReq signInAndOutReq;

    // 音频输入
    private Recorder recorder;
    private Encoder encoder;
    private Sender sender;

    // 音频输出
    private Receiver receiver;
    private Decoder decoder;
    private Tracker tracker;

    public static final int DISCOVERING_SEND = 0;
    public static final int DISCOVERING_RECEIVE = 1;
    public static final int DISCOVERING_LEAVE = 2;

    /**
     * Service与Runnable的通信
     */
    private static class AudioHandler extends Handler {

        private IntercomService service;

        private AudioHandler(IntercomService service) {
            this.service = service;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == DISCOVERING_SEND) {
                Log.i("IntercomService", "发送消息");
            } else if (msg.what == DISCOVERING_RECEIVE) {
                service.findNewUser((String) msg.obj);
            } else if (msg.what == DISCOVERING_LEAVE) {
                service.removeUser((String) msg.obj);
            }
        }
    }

    private Handler handler = new AudioHandler(this);

    /**
     * 发现新的组播成员
     *
     * @param ipAddress IP地址
     */
    private void findNewUser(String ipAddress) {
        final int size = mCallbackList.beginBroadcast();
        for (int i = 0; i < size; i++) {
            IIntercomCallback callback = mCallbackList.getBroadcastItem(i);
            if (callback != null) {
                try {
                    callback.findNewUser(ipAddress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mCallbackList.finishBroadcast();
    }

    /**
     * 删除用户显示
     *
     * @param ipAddress IP地址
     */
    private void removeUser(String ipAddress) {
        final int size = mCallbackList.beginBroadcast();
        for (int i = 0; i < size; i++) {
            IIntercomCallback callback = mCallbackList.getBroadcastItem(i);
            if (callback != null) {
                try {
                    callback.removeUser(ipAddress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mCallbackList.finishBroadcast();
    }

    private RemoteCallbackList<IIntercomCallback> mCallbackList = new RemoteCallbackList<>();

    public IIntercomService.Stub mBinder = new IIntercomService.Stub() {
        @Override
        public void startRecord() throws RemoteException {
            if (!recorder.isRecording()) {
                recorder.setRecording(true);
                tracker.setPlaying(false);
                threadPool.execute(recorder);
            }
        }

        @Override
        public void stopRecord() throws RemoteException {
            if (recorder.isRecording()) {
                recorder.setRecording(false);
                tracker.setPlaying(true);
            }
        }

        @Override
        public void leaveGroup() throws RemoteException {
            // 发送离线消息
            signInAndOutReq.setCommand(Command.DISC_LEAVE);
            threadPool.execute(signInAndOutReq);
        }

        @Override
        public void registerCallback(IIntercomCallback callback) throws RemoteException {
            mCallbackList.register(callback);
        }

        @Override
        public void unRegisterCallback(IIntercomCallback callback) throws RemoteException {
            mCallbackList.unregister(callback);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        showNotification();
    }

    private void initData() {
        // 初始化探测线程
        signInAndOutReq = new SignInAndOutReq(handler);
        signInAndOutReq.setCommand(Command.DISC_REQUEST);
        // 启动探测局域网内其余用户的线程（每分钟扫描一次）
        discoverService.scheduleAtFixedRate(signInAndOutReq, 0, 10, TimeUnit.SECONDS);
        // 初始化JobHandler
        initJobHandler();
    }

    /**
     * 初始化JobHandler
     */
    private void initJobHandler() {
        // 初始化音频输入节点
        recorder = new Recorder(handler);
        encoder = new Encoder(handler);
        sender = new Sender(handler);
        // 初始化音频输出节点
        receiver = new Receiver(handler);
        decoder = new Decoder(handler);
        tracker = new Tracker(handler);
        // 开启音频输入、输出
        threadPool.execute(encoder);
        threadPool.execute(sender);
        threadPool.execute(receiver);
        threadPool.execute(decoder);
        threadPool.execute(tracker);
    }

    /**
     * 前台Service
     */
    private void showNotification() {
        Intent notificationIntent = new Intent(this, AudioActivity.class);
//        notificationIntent.setAction(Command.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.base_app_icon);
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("对讲机")
                .setTicker("对讲机")
                .setContentText("正在使用对讲机")
                .setSmallIcon(R.drawable.base_app_icon)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();

        startForeground(Command.FOREGROUND_SERVICE, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("IntercomService", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放资源
        free();
        // 停止前台Service
        stopForeground(true);
        stopSelf();
    }

    /**
     * 释放系统资源
     */
    private void free() {
        // 释放线程资源
        recorder.free();
        encoder.free();
        sender.free();
        receiver.free();
        decoder.free();
        tracker.free();
        // 释放线程池
        discoverService.shutdown();
        threadPool.shutdown();
    }
}
