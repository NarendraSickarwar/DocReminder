package com.docreminder.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * <h1>RingtoneService  @{@link Service}</h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */


public class RingtoneService extends Service {
    public static final String TYPE = "type";
    public static final int TYPE_PLAY_RINGTONE = 0;
    public static final int TYPE_STOP_RINGTONE = 1;
    public static final String REMINDER_ID = "reminder_id";

    private HashMap<Integer, Ringtone> mRingtones = new HashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int type = intent.getIntExtra(TYPE, -1);
            int reminderID = intent.getIntExtra(REMINDER_ID, -1);
            switch (type) {
                case TYPE_PLAY_RINGTONE:
                    playAudio(getApplicationContext(), reminderID);
                    break;
                case TYPE_STOP_RINGTONE:
                    stopAudio(reminderID);
                    break;
            }
        }
        return START_STICKY;
    }

    private void playAudio(Context context, int reminderID) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();

            mRingtones.put(reminderID, r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAudio(int reminderID) {
        Ringtone r = mRingtones.get(reminderID);
        if (r != null && r.isPlaying()) {
            r.stop();
            mRingtones.remove(reminderID);
        }
    }
}
