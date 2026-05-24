package com.chengyan.cablelock;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WiFiConnectivityHandler wifiConnectivityHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        UIHandler.init(this);
        AlarmPlayer.init(this);
        UsbEventHandler.init(this);
        WifiEventHandler.init(this);

        UIHandler.getInstance().updateAlarmByWifiNames();
    }

    @Override
    protected void onResume() {
        super.onResume();

        UIHandler.getInstance().setupAlarmSelection();
        UIHandler.getInstance().updateAlarmByWifiNames();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!WifiEventHandler.getInstance().onRequestPermissionsResult(requestCode, permissions, results)) {
            super.onRequestPermissionsResult(requestCode, permissions, results);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        wifiConnectivityHandler = new WiFiConnectivityHandler(this);
        wifiConnectivityHandler.startImmediately();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        wifiConnectivityHandler.stopImmediately();
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        if( null == wifiConnectivityHandler) {
            wifiConnectivityHandler = new WiFiConnectivityHandler(this);
            wifiConnectivityHandler.startImmediately();
        }
        super.onPostResume();
    }
}