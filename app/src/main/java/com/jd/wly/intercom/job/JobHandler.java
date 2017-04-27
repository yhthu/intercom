package com.jd.wly.intercom.job;

import android.os.Handler;

import com.jd.wly.intercom.data.AudioData;
import com.jd.wly.intercom.data.MessageQueue;

/**
 * 数据处理节点
 *
 * @author yanghao1
 */
public abstract class JobHandler implements Runnable {

    protected Handler handler;

    public JobHandler(Handler handler) {
        this.handler = handler;
    }

    public void free() {

    }
}
