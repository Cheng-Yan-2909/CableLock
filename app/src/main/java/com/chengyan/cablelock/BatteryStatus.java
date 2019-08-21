package com.chengyan.cablelock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.chengyan.cablelock.exception.ObjectNotInitializedException;

public class BatteryStatus {

    private MainActivity mainActivity = null;
    private Intent batteryStatus = null;
    private static BatteryStatus self = null;

    public static BatteryStatus init(MainActivity mainActivity) {
        if( null == self ) {
            self = new BatteryStatus( mainActivity );
        }
        return self;
    }

    public static BatteryStatus getInstance() {
        if( null == self ) {
            throw new ObjectNotInitializedException("BatteryStatus not initialized");
        }

        return self;
    }

    private BatteryStatus(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        configureBatteryEvent();
    }

    private void configureBatteryEvent() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            public void onReceive (Context context, Intent intent) {
                UIHandler.debugClr();

                try {
                    for (String v : batteryStatus.getCategories()) {
                        UIHandler.debugln(v);
                    }
                }
                catch(Exception e) {

                }

                try {
                    UIHandler.debugln("data string: " +batteryStatus.getDataString());
                }
                catch(Exception e) {

                }

                try {
                    UIHandler.debugln("EXTRA_STATUS: " + batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1));
                    UIHandler.debugln("EXTRA_PLUGGED: " + batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1));
                    UIHandler.debugln("EXTRA_LEVEL: " + batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
                    UIHandler.debugln("EXTRA_SCALE: " + batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
                }
                catch(Exception e) {

                }
            }
        };
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = mainActivity.registerReceiver(broadcastReceiver, ifilter);
    }
}
