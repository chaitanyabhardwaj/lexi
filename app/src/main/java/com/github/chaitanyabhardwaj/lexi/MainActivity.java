package com.github.chaitanyabhardwaj.lexi;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    final private static String TOGGLE_BUTTON_STATE = "<toggle-button-state>";
    final private static String TIME_STATE = "<time-state>";

    private boolean toggleButtonOn = false;

    private int notificationTime;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init activity
        initActivity();
        //initTimeImage();
    }

    public void initActivity() {
        //init layout
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.custom_app_bar);
        }

        //set tag ids to Time tags
        View v1,v2,v3,v4;
        v1 = findViewById(R.id.morning_time);
        v2 = findViewById(R.id.noon_time);
        v3 = findViewById(R.id.evening_time);
        v4 = findViewById(R.id.midnight_time);
        v1.setTag(R.id.TIME_VIEW_TAG_ID,6);
        v2.setTag(R.id.TIME_VIEW_TAG_ID,12);
        v3.setTag(R.id.TIME_VIEW_TAG_ID,18);
        v4.setTag(R.id.TIME_VIEW_TAG_ID,0);

        preferences = this.getPreferences(Context.MODE_PRIVATE);

        if(!preferences.getBoolean(TOGGLE_BUTTON_STATE, true)) {
            //turn on the if false
            View v = findViewById(R.id.button);
            onToggleButtonClick(v);
        }
        else
            initTimeImage();
        //notificationTime = preferences.getInt(TIME_STATE, 6); //default 06:00

    }

    public void initTimeImage() {
        ImageView v1,v2,v3,v4;
        v1 = findViewById(R.id.morning_time);
        v2 = findViewById(R.id.noon_time);
        v3 = findViewById(R.id.evening_time);
        v4 = findViewById(R.id.midnight_time);

        v1.setImageAlpha(150);
        v2.setImageAlpha(150);
        v3.setImageAlpha(150);
        v4.setImageAlpha(150);
    }

    public void onToggleButtonClick(View view) {
        SharedPreferences.Editor editor = preferences.edit();
        Button button = (Button) view;
        if(toggleButtonOn) {
            toggleButtonOn = false;
            button.setBackgroundColor(getResources().getColor(R.color.colorWhiteAccent));
            button.setTextColor(getResources().getColor(R.color.colorBackgroundAccent));
            //turn off the AlarmManager
            setRepeatingNotifications(false);
        }
        else {
            toggleButtonOn = true;
            button.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            button.setTextColor(getResources().getColor(R.color.colorPrimary));
            //turn on the AlarmManager
            setRepeatingNotifications(true);
            int timeChangeId = preferences.getInt(TIME_STATE, R.id.morning_time); //default morning
            onTimeChangeEvent(findViewById(timeChangeId));
        }
        editor.putBoolean(TOGGLE_BUTTON_STATE, !toggleButtonOn);
        editor.apply();

    }

    public void onTimeChangeEvent(View view) {
        SharedPreferences.Editor editor = preferences.edit();
        notificationTime = (int) view.getTag(R.id.TIME_VIEW_TAG_ID);
        Log.d("TAGGED", "NOTIFICATION SET FOR TIME - " + notificationTime);
        setRepeatingNotifications(true);
        //display changed time to user
        String msg = "We'll ship a new word every " + view.getContentDescription();
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
        //change img opacity and change others back to default
        initTimeImage();
        ImageView v = (ImageView) view;
        v.setImageAlpha(255);

        editor.putInt(TIME_STATE, view.getId());
        editor.apply();
    }

    public void setRepeatingNotifications(boolean state) {
        Log.d("TAGGED","SETTING...");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, notificationTime);
        if (calendar.getTimeInMillis()  <= System.currentTimeMillis() + 3000)
        {
            // 3 Second distance
            calendar.add(Calendar.DATE, 1);  // Add 1 day --> Trigger 1 day later from now
        }

        Intent intent = new Intent(getActivity(), LexiAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

        if(!state)
            alarmManager.cancel(alarmIntent);

        Log.d("TAGGED","SET - " + state);
    }

    public Context getActivity() {
        return this;
    }

}
