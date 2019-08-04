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
    TextView debug = null;
    Button stopButton = null;
    private final static int AUDIO_PERMISSION_REQUEST_CODE = 1;
    private final static int VIBRATE_PERMISSION_REQUEST_CODE = 1;
    String mediaFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debug = findViewById(R.id.debug);

        //checkPermissions();
        setupPowerEventReceiver();
        setupStopButton();

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {

            if( ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) ) {
                Toast.makeText(this, "Allow this app to modify alert audio settings", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},
                    AUDIO_PERMISSION_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {

            if( ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.VIBRATE) ) {
                Toast.makeText(this, "Allow this app vibrate on alert", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.VIBRATE},
                    VIBRATE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == VIBRATE_PERMISSION_REQUEST_CODE || requestCode == AUDIO_PERMISSION_REQUEST_CODE ) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Request canceled!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

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

    private void setupPowerEventReceiver() {
        final BroadcastReceiver usbDisconnectReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                stopButton.setEnabled(true);
                playAudio();
            }
        };

        registerReceiver(usbDisconnectReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
    }

    private void playAudio() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.RINGER_MODE_VIBRATE, 20, AudioManager.FLAG_VIBRATE);

            ringtone = RingtoneManager.getRingtone(getApplicationContext(),
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            ringtone.setVolume((float) 20);
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
