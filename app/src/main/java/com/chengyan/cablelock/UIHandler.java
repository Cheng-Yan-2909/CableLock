package com.chengyan.cablelock;

import android.media.RingtoneManager;
import android.net.wifi.ScanResult;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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
    private AlarmTriggerBy alarmTriggerBy = null;
    private TitleText titleText = null;

    private static UIHandler self = null;

    public static void debug(String s) {
        self.debugStuff.debug(s);
    }

    public static void debugln(String s) {
        self.debugStuff.debugln(s);
    }

    public static void debugClr() {
        self.debugStuff.debugClr();
    }

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
        debugStuff = new DebugStuff();
        titleText = new TitleText();
        userAlarmOption = new UserAlarmOption();
        alarmTriggerBy = new AlarmTriggerBy();

        setupStopButton();
        setupEnableButton();
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
                    alarmTriggerBy.resetAlarmTriggerByList();
                    updateAlarmByWifiNames();
                }
            }
        });
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

    public void setupAlarmSelection() {
        userAlarmOption.setupAlarmSelection();
    }

    public String getAlarmByName() {
        return alarmTriggerBy.getAlarmByName();
    }

    public boolean isAlarmByUsb() {
        return alarmTriggerBy.isAlarmByUsb();
    }

    public void updateAlarmByWifiNames() {
        WifiEventHandler.getInstance().updateListener(alarmTriggerBy);
    }

    private void showDebugTools() {
        debugStuff.showDebug();
    }

    /*

     */

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

    private class AlarmTriggerBy implements WifiEventHandler.WifiUpdateListener {
        private int position;
        private String alarmByName;
        private List<String> alarmByNameList = null;
        private boolean wifiDataAdded = false;
        private Spinner alarmBySelector;

        private AlarmTriggerBy() {
            resetAlarmTriggerByList();
            updateAlarmOptionSelector();
            setupAlarmOptionListener();
        }

        private void resetAlarmTriggerByList() {
            alarmByNameList = new ArrayList<String>(){{add("USB");}};
            wifiDataAdded = false;
            position = 0;
        }

        public void updateWifiData(List<ScanResult> optionList) {
            if( !wifiDataAdded ) {
                for (ScanResult sr : optionList) {
                    alarmByNameList.add(sr.SSID);
                    wifiDataAdded = true;
                }
            }
            updateAlarmOptionSelector();
        }

        private void updateAlarmOptionSelector() {
            alarmBySelector = (Spinner) mainActivity.findViewById(R.id.AlarmBy);
            ArrayAdapter<CharSequence> valueAdapter = new ArrayAdapter<CharSequence>(mainActivity, android.R.layout.simple_spinner_item);
            valueAdapter.addAll(alarmByNameList);
            alarmBySelector.setAdapter(valueAdapter);

            if (position > 0) {
                alarmBySelector.setSelection(position);
            }
        }

        private void setupAlarmOptionListener() {
            AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
                public void onItemSelected (AdapterView<?> parent, View view, int _position_, long id) {
                    position = _position_;
                    alarmByName = (String) parent.getItemAtPosition(position);
                    WifiEventHandler.getInstance().clear();
                }

                public void onNothingSelected (AdapterView<?> parent) {

                }
            };

            alarmBySelector.setOnItemSelectedListener(itemSelectedListener);
        }

        private String getAlarmByName() {
            return alarmByName;
        }

        private boolean isAlarmByUsb() {
            return position == 0;
        }
    }

    private class DebugStuff {
        private TextView debug = null;
        private Button debugButton = null;

        private DebugStuff() {
            setupDebug();
        }

        private void setupDebug() {
            setupDebugOutput();
            setupDebugButton();
        }

        private void showDebug() {
            debug.setVisibility(View.VISIBLE);
            debug.bringToFront();
            debugButton.setVisibility(View.VISIBLE);
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

            return (debug.getVisibility() == View.VISIBLE);
        }

        private void setupDebugButton() {
            debugButton = mainActivity.findViewById(R.id.DebugButton);
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
            debugButton.setVisibility(View.INVISIBLE);
        }

        private void setupDebugOutput() {
            debug = mainActivity.findViewById(R.id.DebugOutput);
            debug.setVisibility(View.INVISIBLE);
        }
    }

    private class TitleText {
        private int touchCount = 0;
        private long lastTouchTime = System.currentTimeMillis();
        private long timeoutTouch = 1000; // one sec

        private TitleText() {
            setupTitleText();
        }

        private void setupTitleText() {
            TextView title = mainActivity.findViewById(R.id.TitleView);
            OnTouchListener touchListener = new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if( event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        if( System.currentTimeMillis() > lastTouchTime + timeoutTouch)  {
                            touchCount = 0;
                        }
                        touchCount++;
                        lastTouchTime = System.currentTimeMillis();

                        if (touchCount > 10) {
                            showDebugTools();
                            debugClr();
                        }
                    }
                    return true;
                }
            };
            title.setOnTouchListener(touchListener);
        }
    }
}
