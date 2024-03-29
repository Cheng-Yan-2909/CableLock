package com.chengyan.cablelock;

import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;

import java.util.List;

public class WiFiConnectivityHandler extends Handler implements WifiEventHandler.WifiUpdateListener {

    private final MainActivity mainActivity;

    public static final int MESSAGE_ID = 0;

    public static final long HANDLER_FREQUENCY = 5000;

    public static final long START_IMMEDIATELY = 500;

    private boolean handlerEnabled = true;

    public WiFiConnectivityHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void handleMessage(Message msg) {
        if( !handlerEnabled ) {
            return;
        }

        if( !UIHandler.getInstance().isAlarmByUsb() && UIHandler.getInstance().isAlarmEnabled() ) {
            WifiEventHandler.getInstance().updateListener(this);
        }
    }

    public void startImmediately() {
        this.handlerEnabled = true;
        sendEmptyMessageDelayed(MESSAGE_ID, START_IMMEDIATELY);
    }

    @Override
    public void updateWifiData(List<ScanResult> scanResults) {
        sendEmptyMessageDelayed(MESSAGE_ID, HANDLER_FREQUENCY);
    }

    public void stopImmediately() {
        this.handlerEnabled = false;
        this.removeMessages(MESSAGE_ID);
    }
}
