package com.chengyan.cablelock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class UsbEventHandler {

    private MainActivity mainActivity = null;

    public UsbEventHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setupUsbDisconnectReceiver() {
        final BroadcastReceiver usbDisconnectReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                playAudio();
            }
        };

        mainActivity.registerReceiver(usbDisconnectReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
    }

    private void playAudio() {
        if( !mainActivity.getUIHandler().isAlarmEnabled() ) {
            return;
        }

        mainActivity.getUIHandler().enableStopButton();
        mainActivity.getAlarmPlayer().soundAlarm();
    }
}
