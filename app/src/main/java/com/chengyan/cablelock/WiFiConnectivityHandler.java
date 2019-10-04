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

    public static final long EXTENDED_HANDLER_FREQUENCY = 10000;

    private boolean handlerEnabled = true;

    public WiFiConnectivityHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void handleMessage(Message msg) {
        if( !handlerEnabled ) {
            return;
        }

        long nextCallTime = EXTENDED_HANDLER_FREQUENCY;
        if( !UIHandler.getInstance().isAlarmByUsb() && UIHandler.getInstance().isAlarmEnabled() ) {
            WifiEventHandler.getInstance().updateListener(this);
            nextCallTime = HANDLER_FREQUENCY;
        }
        sendEmptyMessageDelayed(MESSAGE_ID, nextCallTime);
    }

    public void startImmediately() {
        this.handlerEnabled = true;
        sendEmptyMessageDelayed(MESSAGE_ID, START_IMMEDIATELY);
    }

    @Override
    public void updateWifiData(List<ScanResult> scanResults) {

    }

    public void stopImmediately() {
        this.handlerEnabled = false;
        this.removeMessages(MESSAGE_ID);
    }
}
