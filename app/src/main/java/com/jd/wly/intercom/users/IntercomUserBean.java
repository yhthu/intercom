package com.jd.wly.intercom.users;

/**
 * Created by yanghao1 on 2017/4/13.
 */

public class IntercomUserBean {

    private String ipAddress;
    private String aliasName;

    public IntercomUserBean() {
    }

    public IntercomUserBean(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public IntercomUserBean(String ipAddress, String aliasName) {
        this.ipAddress = ipAddress;
        this.aliasName = aliasName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntercomUserBean userBean = (IntercomUserBean) o;

        return ipAddress.equals(userBean.ipAddress);

    }

    @Override
    public int hashCode() {
        return ipAddress.hashCode();
    }
}
