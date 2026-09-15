package com.nerkavach.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DismissAlertReceiver extends BroadcastReceiver {
    private static final String TAG = "DismissAlertReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Notification dismissed or Stop Siren tapped. Silencing alarms immediately.");
        AlertReceiverService.stopAlarm(context);
    }
}
