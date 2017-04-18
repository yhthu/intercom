package com.jd.wly.intercom.input;

import com.jd.wly.intercom.job.JobHandler;

/**
 * Created by yanghao1 on 2017/4/12.
 */

public class Encoder extends JobHandler {

    @Override
    public void handleRequest(byte[] audioData) {
        getNextJobHandler().handleRequest(audioData);
    }
}
