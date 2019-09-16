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
        //BatteryStatus.init(this);
        WifiEventHandler.init(this);
    }
}


