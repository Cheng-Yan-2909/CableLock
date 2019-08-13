package com.chengyan.cablelock;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;

public class AlarmPlayer {

    private Ringtone ringtone = null;

    private MainActivity mainActivity = null;

    public AlarmPlayer(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void soundAlarm() {
        try {
            AudioManager audioManager = (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.RINGER_MODE_VIBRATE, 20, AudioManager.FLAG_VIBRATE);

            ringtone = RingtoneManager.getRingtone(mainActivity.getApplicationContext(),
                    RingtoneManager.getDefaultUri(mainActivity.getUIHandler().getAlarmName()));
            ringtone.setLooping(true);
            ringtone.play();
        } catch (Exception e) {
            stopAlert();
            e.printStackTrace();
        }
    }

    public void stopAlert() {
        if( null == ringtone ) {
            return;
        }
        ringtone.stop();
        ringtone = null;
    }
}
