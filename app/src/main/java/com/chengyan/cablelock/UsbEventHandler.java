package com.chengyan.cablelock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.chengyan.cablelock.exception.ObjectNotInitializedException;

public class UsbEventHandler {

    private MainActivity mainActivity = null;
    private static UsbEventHandler self = null;

    public static UsbEventHandler init(MainActivity mainActivity) {
        if( null == self ) {
            self = new UsbEventHandler(mainActivity);
        }
        return self;
    }

    public static UsbEventHandler getInstance() {
        if( null == self ) {
            throw new ObjectNotInitializedException("UsbEventHandler not initialized");
        }

        return self;
    }

    private UsbEventHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        setupUsbDisconnectReceiver();
    }

    private void setupUsbDisconnectReceiver() {
        final BroadcastReceiver usbDisconnectReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                playAudio();
            }
        };

        mainActivity.registerReceiver(usbDisconnectReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
    }

    private void playAudio() {
        if( !UIHandler.getInstance().isAlarmEnabled() ) {
            return;
        }

        UIHandler.getInstance().enableStopButton();
        AlarmPlayer.getInstance().soundAlarm();
    }
}
