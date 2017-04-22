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

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
