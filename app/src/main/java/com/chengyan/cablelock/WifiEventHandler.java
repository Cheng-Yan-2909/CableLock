package com.chengyan.cablelock;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chengyan.cablelock.exception.ObjectNotInitializedException;

import java.util.List;

public class WifiEventHandler extends EventHandler {

    private final static int PERMISSION_CODE = 1;
    private static WifiEventHandler self = null;
    private final WifiManager wifiManager;
    private WifiUpdateListener wifiUpdateListener = null;
    private int missingSsidCount = 0;

    private Context context = null;

    private static final String[] permissionNameList = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
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

    public void clear() {
        missingSsidCount = 0;
    }

    public void updateListener(WifiUpdateListener wifiUpdateListener) {
        this.wifiUpdateListener = wifiUpdateListener;
        requestWifiScan();
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
            if (!wifiManager.isScanAlwaysAvailable()) {
                UIHandler.debugln("WifFi scan failed 2");
            }
            else {
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
                processScanResult();
            }
        };
        ContextCompat.registerReceiver(mainActivity, wifiEventReceiver, new IntentFilter(WifiManager.NETWORK_IDS_CHANGED_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    private void setupNetworkStateChangedReceiver() {

        final BroadcastReceiver wifiEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dumpWifiInfo(intent);
                processScanResult();
            }
        };
        mainActivity.registerReceiver(wifiEventReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    }

    private void setupWifiStateChangedReceiver() {

        final BroadcastReceiver wifiEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dumpWifiInfo(intent);
                processScanResult();
            }
        };
        mainActivity.registerReceiver(wifiEventReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    private void setupWifiScanResultReceiver() {
        WifiEventHandler wifiEventHandler = this;
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                wifiEventHandler.context = c;
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
            UIHandler.debugln(Manifest.permission.CHANGE_WIFI_STATE + ": Permission granted");
        } else {
            UIHandler.debugln(Manifest.permission.CHANGE_WIFI_STATE + ": Permission denied");
            ActivityCompat.requestPermissions(mainActivity, permissionNameList, PERMISSION_CODE);
        }
    }

    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (requestCode != PERMISSION_CODE) {
            return false;
        }

        if( null != results && results.length > 0 ) {
            if (results[0] == PackageManager.PERMISSION_GRANTED) {
                requestWifiScan();
            } else {
                UIHandler.debugln("User declined permission");
            }
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

    private boolean shouldAlarmByMissingSsid(List<ScanResult> scanResults) {
        missingSsidCount++;
        UIHandler.debugln("WiFi result:");
        for(ScanResult sr : scanResults) {
            UIHandler.debug(sr.SSID + " -- " + sr.toString() + "... ");
            if( sr.SSID.equals(UIHandler.getInstance().getAlarmByName()) ) {
                UIHandler.debugln("found!");
                missingSsidCount--;
            }
            else {
                UIHandler.debugln("not it!");
            }
        }

        UIHandler.debugln("=================\nalarm by: " + UIHandler.getInstance().getAlarmByName());

        return !UIHandler.getInstance().isAlarmByUsb() && (missingSsidCount > 1);
    }

    private void processScanResult() {
        if(this.context == null) return;
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            List<ScanResult> scanResults = wifiManager.getScanResults();
            if (null != wifiUpdateListener) {
                wifiUpdateListener.updateWifiData(scanResults);
            }
            wifiUpdateListener = null;

            if (shouldAlarmByMissingSsid(scanResults)) {
                playAudio();
            }
        }
    }

    public interface WifiUpdateListener {
        void updateWifiData(List<ScanResult> scanResults);
    }
}
