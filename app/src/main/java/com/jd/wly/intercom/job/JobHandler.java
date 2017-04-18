package com.jd.wly.intercom.job;

/**
 * Created by yanghao1 on 2017/4/12.
 */

public abstract class JobHandler {

    private JobHandler nextJobHandler;

    public JobHandler getNextJobHandler() {
        return nextJobHandler;
    }

    public void setNextJobHandler(JobHandler nextJobHandler) {
        this.nextJobHandler = nextJobHandler;
    }

    public abstract void handleRequest(byte[] audioData);

    /**
     * 释放资源
     */
    public void free() {

    }
}
