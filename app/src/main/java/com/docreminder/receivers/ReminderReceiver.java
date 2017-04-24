package com.docreminder.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.docreminder.R;
import com.docreminder.app.details.ReminderDetailsActivity;
import com.docreminder.services.RingtoneService;

import static android.R.attr.id;

/**
 * <h1>ReminderReceiver  @{@link WakefulBroadcastReceiver}</h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */

public class ReminderReceiver extends WakefulBroadcastReceiver {
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION_TYPE = "notification_type";
    public static final int SIMPLE_NOTIFICATION = 0;
    public static final int AUDIO_NOTIFICATION = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        int type = intent.getIntExtra(NOTIFICATION_TYPE, SIMPLE_NOTIFICATION);

        if (type == SIMPLE_NOTIFICATION) {
            showNotification(context, id, "Reminder Alert", "Please check your appointments");
            setAudioReminder(context, id);
        } else {
            showNotification(context, id, "Reminder Alert", "Time for your appointment");
            playAudio(context, id);
            wakeScreen(context);
        }

        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void showNotification(Context context, int reminderID, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notiBuilder = new Notification.Builder(context);

        Intent detailIntent = new Intent(context, ReminderDetailsActivity.class);
        detailIntent.putExtra(ReminderDetailsActivity.DETAIL_TYPE, ReminderDetailsActivity.VIEW_ENTRY);
        detailIntent.putExtra(ReminderDetailsActivity.FROM_NOTIFICATION, true);
        detailIntent.putExtra(ReminderDetailsActivity.REMINDER_ID, reminderID);
        PendingIntent pi = PendingIntent.getActivity(context, 0, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notiBuilder.setContentIntent(pi);

        Intent dismissIntent = new Intent(context, RingtoneDismissReceiver.class);
        dismissIntent.putExtra(RingtoneDismissReceiver.REMINDER_ID, reminderID);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, reminderID, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notiBuilder.setDeleteIntent(dismissPendingIntent);

        notiBuilder.setAutoCancel(true);
        notiBuilder.setContentTitle(title);
        notiBuilder.setContentText(message);
        notiBuilder.setSmallIcon(R.mipmap.ic_launcher);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notiBuilder.setSound(alarmSound);

        notiBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        notiBuilder.setLights(Color.RED, 3000, 3000);

        Notification notification = notiBuilder.build();

        notificationManager.notify(id, notification);
    }

    private void setAudioReminder(Context context, int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.NOTIFICATION_ID, id);
        intent.putExtra(ReminderReceiver.NOTIFICATION_TYPE, ReminderReceiver.AUDIO_NOTIFICATION);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int currentMinute = cal.get(java.util.Calendar.MINUTE);
        cal.set(java.util.Calendar.MINUTE, currentMinute + 1);
        long alarmMillis = cal.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmMillis, pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmMillis, pi);
        }
    }

    private void playAudio(Context context, int reminderID) {
        Intent intent = new Intent(context, RingtoneService.class);
        intent.putExtra(RingtoneService.TYPE, RingtoneService.TYPE_PLAY_RINGTONE);
        intent.putExtra(RingtoneService.REMINDER_ID, reminderID);
        context.startService(intent);
    }

    private void wakeScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!isScreenOn) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
            wl_cpu.acquire(10000);
        }


    }


}
