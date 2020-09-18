package com.example.personalbutler.viewModel;

/**
 * @description: 用户登录传递的requestBody类型
 * @author: Mask
 * @time: 2020/9/18 3:23 下午
 */
public class PostLoginModel {
    private String userName;
    private String userPassword;
    private boolean rememberMe;
    private String deviceType;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
