package com.chengyan.cablelock;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private AlarmPlayer alarmPlayer = null;
    private UsbEventHandler usbEventHandler = null;
    private UIHandler uiHandler = null;

    public AlarmPlayer getAlarmPlayer() {
        return alarmPlayer;
    }

    public UIHandler getUIHandler() {
        return uiHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmPlayer = new AlarmPlayer(this);
        uiHandler = new UIHandler(this);

        setupUsbDisconnectReceiver();
    }

    private void setupUsbDisconnectReceiver() {
        usbEventHandler = new UsbEventHandler(this);
        usbEventHandler.setupUsbDisconnectReceiver();
    }
}
