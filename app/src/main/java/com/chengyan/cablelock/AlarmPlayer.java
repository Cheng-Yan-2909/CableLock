package com.chengyan.cablelock;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;

import com.chengyan.cablelock.exception.ObjectNotInitializedException;
import android.util.Log;

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

    private void alarmLogger(String msg) {
        Log.i(this.getClass().getName(), msg);
        UIHandler.debug(this.getClass().getName() + msg + "\n");
    }
    public void soundAlarm() {
        try {
            alarmLogger("Sound alarm");
            if( null == ringtone ) {
                alarmLogger("Get ringtone");
                AudioManager audioManager = (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.RINGER_MODE_VIBRATE, 80, AudioManager.FLAG_VIBRATE);

                ringtone = RingtoneManager.getRingtone(mainActivity.getApplicationContext(),
                        RingtoneManager.getDefaultUri(UIHandler.getInstance().getAlarmName()));
                alarmLogger("Ringtone: " + ringtone.toString());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    alarmLogger(" -- set looping");
                    ringtone.setLooping(true);
                }
                ringtone.play();
                alarmLogger("Alarm sounded");
            }
        } catch (Exception e) {
            alarmLogger("Failed to sound alarm: " + e.toString());
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
