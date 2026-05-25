package com.chengyan.cablelock;

public class EventHandler {

    protected final MainActivity mainActivity;

    protected EventHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private void logger(String msg) {
        UIHandler.debug(msg + "\n");
    }
    protected void playAudio() {
        if( !UIHandler.getInstance().isAlarmEnabled() ) {
            return;
        }

        logger("Alarm - enabling 'stop' button");
        UIHandler.getInstance().enableStopButton();

        logger("Alarm - sound alarm");
        AlarmPlayer.getInstance().soundAlarm();
    }

}
