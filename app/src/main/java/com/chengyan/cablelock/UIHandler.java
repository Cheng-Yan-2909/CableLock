package com.chengyan.cablelock;

import android.media.RingtoneManager;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.chengyan.cablelock.exception.ObjectNotInitializedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIHandler {

    private MainActivity mainActivity = null;

    private Button stopButton = null;
    private Button enableButton = null;
    private boolean alarmEnabled = true;

    private UserAlarmOption userAlarmOption = null;
    private DebugStuff debugStuff = null;
    private static UIHandler self = null;

    public static UIHandler init(MainActivity mainActivity) {
        if( null == self ) {
            self = new UIHandler(mainActivity);
        }
        return self;
    }

    public static UIHandler getInstance() {
        if( null == self ) {
            throw new ObjectNotInitializedException("UIHandler not initialized");
        }
        return self;
    }

    private UIHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        configureUI();
    }

    private void configureUI() {
        userAlarmOption = new UserAlarmOption();

        setupStopButton();
        setupEnableButton();
        debugStuff = new DebugStuff();
    }

    public boolean isAlarmEnabled() {
        return alarmEnabled;
    }

    public void enableStopButton() {
        stopButton.setEnabled(true);
    }

    public int getAlarmName() {
        return userAlarmOption.getAlarmName();
    }

    public static void debug(String s) {
        self.debugStuff.debug(s);
    }

    public static void debugln(String s) {
        self.debugStuff.debugln(s);
    }

    public static void debugClr() {
        self.debugStuff.debugClr();
    }

    private void setupStopButton() {
        stopButton = mainActivity.findViewById(R.id.StopButton);
        stopButton.setEnabled(false);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmPlayer.getInstance().stopAlert();
                stopButton.setEnabled(false);
            }
        });
    }

    private void setupEnableButton() {
        enableButton = mainActivity.findViewById(R.id.EnableButton);

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
                    WifiEventHandler.getInstance().requestWifiScan();
                }
            }
        });
    }

    public void setupAlarmSelection() {
        userAlarmOption.setupAlarmSelection();
    }

    private class UserAlarmOption {

        private Map<String, Integer> alarmValueMap = new HashMap(){{
            put("Ring Tone", RingtoneManager.TYPE_RINGTONE) ;
            put("Notification", RingtoneManager.TYPE_NOTIFICATION);
            put("Alarm", RingtoneManager.TYPE_ALARM);
        }};

        private int position = -1;

        private String alarmName = "Notification";

        private UserAlarmOption() {
            setupAlarmSelection();
        }

        private void setupAlarmSelection() {
            Spinner alarmSelector = (Spinner) mainActivity.findViewById(R.id.AlarmSelector);
            ArrayAdapter<CharSequence> valueAdapter = new ArrayAdapter<CharSequence>(mainActivity, android.R.layout.simple_spinner_item);
            for(String name : alarmValueMap.keySet() ) {
                valueAdapter.add( name );
            }
            alarmSelector.setAdapter(valueAdapter);

            if( position > -1 ) {
                alarmSelector.setSelection(position);
            }

            AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
                public void onItemSelected (AdapterView<?> parent, View view, int _position_, long id) {
                    position = _position_;
                    alarmName = (String) parent.getItemAtPosition(position);
                }

                public void onNothingSelected (AdapterView<?> parent) {

                }
            };

            alarmSelector.setOnItemSelectedListener(itemSelectedListener);
        }

        private int getAlarmName() {
            return alarmValueMap.get(alarmName);
        }
    }

    private class AlarmTriggerBy {

        private AlarmTriggerBy() {

        }

        private List<String> getAlarmTriggerOptions() {
            List<String> triggerOptionList = new ArrayList() {{
                add("USB");
            }};

            return triggerOptionList;
        }
    }

    private class DebugStuff {
        private TextView debug = null;
        private Button debugButton = null;

        private DebugStuff() {
            setupDebugOutput();
            setupDebugButton();
        }

        private void debug(String s) {
            if( !isDebugEnabled() ) {
                return;
            }

            try {
                s = debug.getText() + s;
                debug.setText(s);
            }
            catch(Exception e) {

            }
        }

        private void debugln(String s) {
            debug(s + "\n");
        }

        private void debugClr() {
            if( !isDebugEnabled() ) {
                return;
            }
            try {
                debug.setText("");
            }
            catch(Exception e) {

            }
        }

        private boolean isDebugEnabled() {
            if( null == self ) {
                return false;
            }
            if( null == debug ) {
                return false;
            }

            return true;
        }

        private void setupDebugButton() {
            debugButton = mainActivity.findViewById(R.id.DebugButton);

            if( isOnDebugDevice() ) {
                debugButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (debug.getVisibility() == View.VISIBLE) {
                            debug.setVisibility(View.INVISIBLE);
                        } else {
                            debug.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            else {
                ((ViewGroup) debugButton.getParent()).removeView(debugButton);
                debugButton = null;
            }
        }

        private void setupDebugOutput() {
            debug = mainActivity.findViewById(R.id.DebugOutput);
            if( isOnDebugDevice() ) {
                debug.setText("Debug enabled\n");
            }
            else {
                ((ViewGroup) debug.getParent()).removeView(debug);
                debug = null;
            }
        }

        private boolean isOnDebugDevice() {
            return Build.ID.equals("PPWS29.69-39-2-4");
        }
    }
}
