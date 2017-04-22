package com.jd.wly.intercom.util;

import android.media.audiofx.AcousticEchoCanceler;

/**
 * Created by yanghao1 on 2017/4/18.
 */

public class AECUtil {

    private static AcousticEchoCanceler canceler;

    public static boolean initAEC(int audioSession) {
        if (canceler != null) {
            return false;
        }
        canceler = AcousticEchoCanceler.create(audioSession);
        canceler.setEnabled(true);
        return canceler.getEnabled();
    }

    public static boolean isDeviceSupport() {
        return AcousticEchoCanceler.isAvailable();
    }

    public static boolean setAECEnabled(boolean enable) {
        if (null == canceler) {
            return false;
        }
        canceler.setEnabled(enable);
        return canceler.getEnabled();
    }

    public static boolean release() {
        if (null == canceler) {
            return false;
        }
        canceler.setEnabled(false);
        canceler.release();
        canceler = null;
        return true;
    }
}
