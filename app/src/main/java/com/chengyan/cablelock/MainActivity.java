package com.chengyan.cablelock;

import androidx.appcompat.app.AppCompatActivity;
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
}


