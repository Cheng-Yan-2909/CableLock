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

    Ringtone ringtone = null;
    Button stopButton = null;
    Button enableButton = null;
    boolean alarmEnabled = true;
    Map<String, Integer> alarmValueMap = new HashMap(){{
       put("Ring Tone", RingtoneManager.TYPE_RINGTONE) ;
       put("Notification", RingtoneManager.TYPE_NOTIFICATION);
       put("Alarm", RingtoneManager.TYPE_ALARM);
    }};
    String alarmName = "Notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupAlarmSelection();
        setupPowerEventReceiver();
        setupStopButton();
        setupEnableButton();
    }

    private void setupAlarmSelection() {
        Spinner alarmSelector = (Spinner) findViewById(R.id.AlarmSelector);
        ArrayAdapter<CharSequence> valueAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        for(String name : alarmValueMap.keySet() ) {
            valueAdapter.add( name );
        }
        alarmSelector.setAdapter(valueAdapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
                alarmName = (String) parent.getItemAtPosition(position);
            }

            public void onNothingSelected (AdapterView<?> parent) {

            }
        };

        alarmSelector.setOnItemSelectedListener(itemSelectedListener);
    }

    private void setupStopButton() {
        stopButton = findViewById(R.id.StopButton);
        stopButton.setEnabled(false);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlert();
            }
        });
    }

    private void setupEnableButton() {
        enableButton = findViewById(R.id.EnableButton);

        enableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( alarmEnabled ) {
                    enableButton.setText("Alarm Disabled");
                    alarmEnabled = false;
                }
                else {
                    enableButton.setText("Alarm Enabled");
                    alarmEnabled = true;
                }
            }
        });
    }

    private void setupPowerEventReceiver() {
        final BroadcastReceiver usbDisconnectReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                playAudio();
            }
        };

        registerReceiver(usbDisconnectReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
    }

    private void playAudio() {
        if( !alarmEnabled ) {
            return;
        }

        stopButton.setEnabled(true);
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.RINGER_MODE_VIBRATE, 20, AudioManager.FLAG_VIBRATE);

            ringtone = RingtoneManager.getRingtone(getApplicationContext(),
                    RingtoneManager.getDefaultUri(alarmValueMap.get(alarmName)));
            ringtone.setLooping(true);
            ringtone.play();
        } catch (Exception e) {
            stopAlert();
            e.printStackTrace();
        }
    }

    private void stopAlert() {
        if( null != ringtone ) {
            ringtone.stop();
        }
        ringtone = null;
        stopButton.setEnabled(false);
    }
}
