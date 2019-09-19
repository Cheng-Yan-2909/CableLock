package com.chengyan.cablelock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import androidx.core.app.ActivityCompat;
import com.chengyan.cablelock.exception.ObjectNotInitializedException;
import java.util.List;
import android.net.wifi.WifiInfo;
import android.os.Build;

public class WifiEventHandler extends EventHandler {

    private final static int PERMISSION_CODE = 1;

    private static WifiEventHandler self = null;

    private final WifiManager wifiManager;

    private static final String[] permissionNameList = new String[] {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CHANGE_WIFI_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.ACCESS_NETWORK_STATE
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
        UIHandler.debugln("ID: " + Build.ID);
        UIHandler.debugln("MODEL: " + Build.MODEL);
        UIHandler.debugln("DISPLAY: " + Build.DISPLAY);
        UIHandler.debugln("HOST: " + Build.HOST);
        UIHandler.debugln("PRODUCT: " + Build.PRODUCT);
        UIHandler.debugln("DEVICE: " + Build.DEVICE);
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
        setupWifiScanResultReceiver();
        setupNetworkIDChangedReceiver();
        setupNetworkStateChangedReceiver();
        setupWifiStateChangedReceiver();
    }

    private void setupNetworkIDChangedReceiver() {

        final BroadcastReceiver wifiEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dumpWifiInfo(intent);
            }
        };
        mainActivity.registerReceiver(wifiEventReceiver, new IntentFilter(WifiManager.NETWORK_IDS_CHANGED_ACTION));
    }

    private void setupNetworkStateChangedReceiver() {

        final BroadcastReceiver wifiEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dumpWifiInfo(intent);
            }
        };
        mainActivity.registerReceiver(wifiEventReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    }

    private void setupWifiStateChangedReceiver() {

        final BroadcastReceiver wifiEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dumpWifiInfo(intent);
            }
        };
        mainActivity.registerReceiver(wifiEventReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    private void setupWifiScanResultReceiver() {
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                UIHandler.debugln(intent.getAction());
                processScanResult();
            }
        };

        mainActivity.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        requestWifiScan();
    }

    public boolean gotAllPermissions() {
        for(String perm : permissionNameList ) {
            if( ActivityCompat.checkSelfPermission(mainActivity.getApplicationContext(), perm) != PackageManager.PERMISSION_GRANTED ) {
                return false;
            }
        }
        return true;
    }

    public void configPermission() {
        if (gotAllPermissions()) {
            UIHandler.debugln(android.Manifest.permission.CHANGE_WIFI_STATE + ": Permission granted");
        } else {
            UIHandler.debugln(android.Manifest.permission.CHANGE_WIFI_STATE + ": Permission denied");
            ActivityCompat.requestPermissions(mainActivity, permissionNameList, PERMISSION_CODE);
        }
    }

    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (requestCode != PERMISSION_CODE) {
            return false;
        }

        if (results[0] == PackageManager.PERMISSION_GRANTED) {
            requestWifiScan();
        } else {
            UIHandler.debugln("User declined permission");
        }

        return true;
    }

    private void dumpWifiInfo(Intent intent) {
        UIHandler.debugln(intent.getAction());
        UIHandler.debugln("WiFi info:");
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        UIHandler.debugln( "SSID: " + wifiInfo.getSSID());
        UIHandler.debugln( "wifi info toString:\n" + wifiInfo + "\n==============\n" );
    }

    private void processScanResult() {
        List<ScanResult> scanResults = wifiManager.getScanResults();
        UIHandler.debugln("WiFi result:");
        for(ScanResult sr : scanResults) {
            UIHandler.debugln(sr.SSID + " -- " + sr.toString() + "\n==============");
        }
    }
}
