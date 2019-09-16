package com.chengyan.cablelock;

import android.media.RingtoneManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.chengyan.cablelock.exception.ObjectNotInitializedException;

import java.util.HashMap;
import java.util.Map;

public class UIHandler {

    private MainActivity mainActivity = null;

    private Button stopButton = null;
    private Button enableButton = null;
    private boolean alarmEnabled = true;
    private TextView debug = null;
    private UserAlarmOption userAlarmOption = null;
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
        setupDebugOutput();
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
        if( !isDebugEnabled() ) {
            return;
        }

        try {
            s = self.debug.getText() + s;
            self.debug.setText(s);
        }
        catch(Exception e) {

        }
    }

    public static void debugln(String s) {
        debug(s + "\n");
    }

    public static void debugClr() {
        if( !isDebugEnabled() ) {
            return;
        }
        try {
            self.debug.setText("");
        }
        catch(Exception e) {

        }
    }

    private static boolean isDebugEnabled() {
        if( null == self ) {
            return false;
        }
        if( null == self.debug ) {
            return false;
        }

        return true;
    }

    private void setupDebugOutput() {
        debug = mainActivity.findViewById(R.id.DebugOutput);
        debug.setText("Debug enabled\n");
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

    public class UserAlarmOption {

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
}
