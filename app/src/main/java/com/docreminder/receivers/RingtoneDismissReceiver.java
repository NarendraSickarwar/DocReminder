package com.docreminder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.docreminder.services.RingtoneService;

/**
 * <h1>RingtoneDismissReceiver  @{@link BroadcastReceiver}</h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */

public class RingtoneDismissReceiver extends BroadcastReceiver {
    public static final String REMINDER_ID = "reminder_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderID = intent.getIntExtra(REMINDER_ID, -1);

        Intent serviceIntent = new Intent(context, RingtoneService.class);
        serviceIntent.putExtra(RingtoneService.TYPE, RingtoneService.TYPE_STOP_RINGTONE);
        serviceIntent.putExtra(RingtoneService.REMINDER_ID, reminderID);
        context.startService(serviceIntent);
    }
}
