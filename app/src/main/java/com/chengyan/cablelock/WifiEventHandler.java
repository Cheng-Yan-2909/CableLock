package com.chengyan.cablelock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.chengyan.cablelock.exception.ObjectNotInitializedException;

public class WifiEventHandler extends EventHandler {

    private static WifiEventHandler self = null;
    private final WifiManager wifiManager;

    public static WifiEventHandler init(MainActivity mainActivity) {
        if( null == self ) {
            self = new WifiEventHandler(mainActivity);
        }
        return self;
    }

    public static WifiEventHandler getInstance() {
        if( null == self ) {
            throw new ObjectNotInitializedException("UsbEventHandler not initialized");
        }

        return self;
    }

    private WifiEventHandler(MainActivity mainActivity) {
        super(mainActivity);
        wifiManager = (WifiManager) mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    private void setupWifiEvent() {

    }
}
