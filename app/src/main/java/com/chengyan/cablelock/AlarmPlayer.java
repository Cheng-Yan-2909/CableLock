package com.chengyan.cablelock;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;

import com.chengyan.cablelock.exception.ObjectNotInitializedException;

public class AlarmPlayer {

    private Ringtone ringtone = null;
    private MainActivity mainActivity = null;
    private static AlarmPlayer self = null;

    public static AlarmPlayer init(MainActivity mainActivity) {
        if( null == self ) {
            self = new AlarmPlayer(mainActivity);
        }
        return self;
    }

    public static AlarmPlayer getInstance() {
        if( null == self ) {
            throw new ObjectNotInitializedException("AlarmPlayer not initialized");
        }
        return self;
    }

    private AlarmPlayer(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void soundAlarm() {
        try {
            if( null == ringtone ) {
                AudioManager audioManager = (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.RINGER_MODE_VIBRATE, 20, AudioManager.FLAG_VIBRATE);

                ringtone = RingtoneManager.getRingtone(mainActivity.getApplicationContext(),
                        RingtoneManager.getDefaultUri(UIHandler.getInstance().getAlarmName()));
                ringtone.setLooping(true);
                ringtone.play();
            }
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
