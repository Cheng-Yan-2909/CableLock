package com.chengyan.cablelock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UIHandler.init(this);

        AlarmPlayer.init(this);
        UsbEventHandler.init(this);
        WifiEventHandler.init(this);

        WifiEventHandler.getInstance().updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        UIHandler.getInstance().setupAlarmSelection();

        if( WifiEventHandler.getInstance().gotAllPermissions() ) {
            WifiEventHandler.getInstance().requestWifiScan();
        }
        else {
            WifiEventHandler.getInstance().configPermission();
        }

        WifiEventHandler.getInstance().updateUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!WifiEventHandler.getInstance().onRequestPermissionsResult(requestCode, permissions, results)) {
            super.onRequestPermissionsResult(requestCode, permissions, results);
        }
    }
}


