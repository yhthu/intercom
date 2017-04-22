package com.jd.wly.intercom.job;

/**
 * 数据处理节点
 *
 * @param <I> 输入数据类型
 * @param <O> 输出数据类型
 * @author yanghao1
 */
public abstract class JobHandler<I, O> {

    private JobHandler<O, ?> nextJobHandler;

    public JobHandler<O, ?> getNextJobHandler() {
        return nextJobHandler;
    }

    public void setNextJobHandler(JobHandler<O, ?> nextJobHandler) {
        this.nextJobHandler = nextJobHandler;
    }

    public abstract void handleRequest(I audioData);

    /**
     * 释放资源
     */
    public void free() {

    }
}
