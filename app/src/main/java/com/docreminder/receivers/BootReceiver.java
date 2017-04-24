package com.docreminder.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.docreminder.services.ReminderService;

/**
 * <h1>BootReceiver  @{@link WakefulBroadcastReceiver}</h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */


public class BootReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive boot receiver");
        Intent serviceIntent = new Intent(context, ReminderService.class);
        serviceIntent.putExtra(ReminderService.INTENT_TASK, ReminderService.TASK_ADD_ALL_ALARMS);

        startWakefulService(context, serviceIntent);
    }
}
