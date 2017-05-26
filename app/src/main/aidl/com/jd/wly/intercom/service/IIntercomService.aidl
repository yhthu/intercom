package com.jd.wly.intercom.service;

import com.jd.wly.intercom.service.IIntercomCallback;

interface IIntercomService {

    void startRecord();
    void stopRecord();
    void leaveGroup();
    void registerCallback(IIntercomCallback callback);
    void unRegisterCallback(IIntercomCallback callback);
}
