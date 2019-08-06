package com.chengyan.cablelock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    Ringtone ringtone = null;
    Button stopButton = null;
    Button enableButton = null;
    boolean alarmEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupPowerEventReceiver();
        setupStopButton();
        setupEnableButton();
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
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
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
