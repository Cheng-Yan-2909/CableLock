package com.chengyan.cablelock;

public class EventHandler {

    protected final MainActivity mainActivity;

    protected EventHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    protected void playAudio() {
        if( !UIHandler.getInstance().isAlarmEnabled() ) {
            return;
        }

        UIHandler.getInstance().enableStopButton();
        AlarmPlayer.getInstance().soundAlarm();
    }

}
