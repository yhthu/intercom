package com.jd.wly.intercom;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.TextView;

import com.jd.wly.intercom.service.IIntercomCallback;
import com.jd.wly.intercom.service.IIntercomService;
import com.jd.wly.intercom.service.IntercomService;
import com.jd.wly.intercom.users.IntercomAdapter;
import com.jd.wly.intercom.users.IntercomUserBean;
import com.jd.wly.intercom.users.VerticalSpaceItemDecoration;
import com.jd.wly.intercom.util.IPUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AudioActivity extends Activity {

    private RecyclerView localNetworkUser;
    private TextView currentIp;

    private List<IntercomUserBean> userBeanList = new ArrayList<>();
    private IntercomAdapter intercomAdapter;

    /**
     * onServiceConnected和onServiceDisconnected运行在UI线程中
     */
    private IIntercomService intercomService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            intercomService = IIntercomService.Stub.asInterface(service);
            try {
                intercomService.registerCallback(intercomCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            intercomService = null;
        }
    };

    /**
     * 被调用的方法运行在Binder线程池中，不能更新UI
     */
    private IIntercomCallback intercomCallback = new IIntercomCallback.Stub() {
        @Override
        public void findNewUser(String ipAddress) throws RemoteException {
            sendMsg2MainThread(ipAddress, FOUND_NEW_USER);
        }

        @Override
        public void removeUser(String ipAddress) throws RemoteException {
            sendMsg2MainThread(ipAddress, REMOVE_USER);
        }
    };

    private static final int FOUND_NEW_USER = 0;
    private static final int REMOVE_USER = 1;

    /**
     * 跨进程回调更新界面
     */
    private static class DisplayHandler extends Handler {
        // 弱引用
        private WeakReference<AudioActivity> activityWeakReference;

        DisplayHandler(AudioActivity audioActivity) {
            activityWeakReference = new WeakReference<>(audioActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AudioActivity activity = activityWeakReference.get();
            if (activity != null) {
                if (msg.what == FOUND_NEW_USER) {
                    activity.foundNewUser((String) msg.obj);
                } else if (msg.what == REMOVE_USER) {
                    activity.removeExistUser((String) msg.obj);
                }
            }
        }
    }

    private Handler handler = new DisplayHandler(this);

    /**
     * 发送Handler消息
     *
     * @param content 内容
     * @param msgWhat 消息类型
     */
    private void sendMsg2MainThread(String content, int msgWhat) {
        Message msg = new Message();
        msg.what = msgWhat;
        msg.obj = content;
        handler.sendMessage(msg);
    }

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

    private void initData() {
        // 初始化AudioManager配置
        initAudioManager();
        // 启动Service
        Intent intent = new Intent(AudioActivity.this, IntercomService.class);
        startService(intent);
    }

    /**
     * 初始化AudioManager配置
     */
    private void initAudioManager() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(AudioActivity.this, IntercomService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 更新自身IP
     */
    public void updateMyself() {
        currentIp.setText(IPUtil.getLocalIPAddress());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_F2 ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            try {
                intercomService.startRecord();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_F2 ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            try {
                intercomService.stopRecord();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // 发送离开群组消息
        try {
            intercomService.leaveGroup();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
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
     * 删除用户
     *
     * @param ipAddress
     */
    public void removeExistUser(final String ipAddress) {
        IntercomUserBean userBean = new IntercomUserBean(ipAddress);
        if (userBeanList.contains(userBean)) {
            int position = userBeanList.indexOf(userBean);
            userBeanList.remove(position);
            intercomAdapter.notifyItemRemoved(position);
            intercomAdapter.notifyItemRangeChanged(0, userBeanList.size());
        }
    }

    /**
     * 增加新的用户
     *
     * @param userBean 新用户
     */
    public void addNewUser(IntercomUserBean userBean) {
        userBeanList.add(userBean);
        intercomAdapter.notifyItemInserted(userBeanList.size() - 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (intercomService != null && intercomService.asBinder().isBinderAlive()) {
            try {
                intercomService.unRegisterCallback(intercomCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(serviceConnection);
        }
    }
}
