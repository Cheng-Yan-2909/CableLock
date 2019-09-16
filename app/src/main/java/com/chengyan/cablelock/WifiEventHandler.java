package com.chengyan.cablelock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import androidx.core.app.ActivityCompat;

import com.chengyan.cablelock.exception.ObjectNotInitializedException;

import java.util.ArrayList;
import java.util.List;

public class WifiEventHandler extends EventHandler {

    public final static int PERMISSION_CODE = 1;

    private static WifiEventHandler self = null;

    private final WifiManager wifiManager;

    public static final String[] permissionNameList = new String[] {
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.CHANGE_WIFI_STATE,
        android.Manifest.permission.ACCESS_WIFI_STATE
    };

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

    public void requestWifiScan() {
        UIHandler.debugClr();
        UIHandler.debugln("Request WiFi scan");

        configPermission();

        try {
            if (!wifiManager.startScan()) {
                UIHandler.debugln("WifFi scan failed 2");
            }
        }
        catch(Exception e) {
            UIHandler.debugln("Error scan: " + e.getMessage());
        }

    }

    private WifiEventHandler(MainActivity mainActivity) {
        super(mainActivity);
        wifiManager = (WifiManager) mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        setupWifiEvent();
    }

    private void setupWifiEvent() {
        UIHandler.debugln("WiFi event setup...");

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                processScanResult();
            }
        };

        mainActivity.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        requestWifiScan();

        UIHandler.debugln("WiFi event setup: done");
    }

    private void configPermission() {
        UIHandler.debugln("config permission");
        boolean changeWifiStatePermission = ActivityCompat.checkSelfPermission(mainActivity.getApplicationContext(), android.Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED;
        boolean accessFineLocationPermission = ActivityCompat.checkSelfPermission(mainActivity.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean accessWifiStatePermission = ActivityCompat.checkSelfPermission(mainActivity.getApplicationContext(), android.Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED;

        if (changeWifiStatePermission && accessFineLocationPermission && accessWifiStatePermission) {
            UIHandler.debugln(android.Manifest.permission.CHANGE_WIFI_STATE + ": Permission granted");
        } else {
            UIHandler.debugln(android.Manifest.permission.CHANGE_WIFI_STATE + ": Permission denied");
            ActivityCompat.requestPermissions(mainActivity, permissionNameList, PERMISSION_CODE);
        }
    }

    private void processScanResult() {
        List<ScanResult> scanResults = wifiManager.getScanResults();
        UIHandler.debugln("WiFi result:");
        for(ScanResult sr : scanResults) {
            UIHandler.debugln(sr.SSID + " -- " + sr.toString() + "\n==============");
        }
    }
}
