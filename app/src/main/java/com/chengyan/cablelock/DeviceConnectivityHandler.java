package com.chengyan.cablelock;

import android.os.Handler;
import android.os.Message;

public class DeviceConnectivityHandler extends Handler {

    private final MainActivity mainActivity;

    public DeviceConnectivityHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void handleMessage(Message msg) {

    }
}
