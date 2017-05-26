package com.jd.wly.intercom.service;

interface IIntercomCallback {

    void findNewUser(String ipAddress);
    void removeUser(String ipAddress);
}
