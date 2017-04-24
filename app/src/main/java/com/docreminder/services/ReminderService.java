package com.docreminder.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.docreminder.db.ReminderTable;
import com.docreminder.models.ReminderModel;
import com.docreminder.receivers.ReminderReceiver;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * <h1>ReminderService  @{@link Service}</h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */

public class ReminderService extends Service {
    public static final String INTENT_TASK = "intent_task";
    public static final String INTENT_REMINDER_ID = "intent_reminder_id";
    public static final int DEFAULT = -1;
    public static final int TASK_ADD_ALL_ALARMS = 0;
    public static final int TASK_ADD_SINGLE_ALARM = 1;
    public static final int TASK_REMOVE_ALARM = 2;
    private static final String TAG = "ReminderService";
    private AlarmManager mAlarmManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + "ReminderService");
        if (intent != null) {
            int taskID = intent.getIntExtra(INTENT_TASK, DEFAULT);
            switch (taskID) {
                case TASK_ADD_ALL_ALARMS:
                    setAllAlarms();
                    WakefulBroadcastReceiver.completeWakefulIntent(intent);
                    break;
                case TASK_ADD_SINGLE_ALARM:
                    int reminderID = intent.getIntExtra(INTENT_REMINDER_ID, DEFAULT);
                    setSingleAlarm(reminderID);
                    break;
                case TASK_REMOVE_ALARM:
                    reminderID = intent.getIntExtra(INTENT_REMINDER_ID, DEFAULT);
                    removeAlarm(reminderID);
                    break;
            }
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    private void setAllAlarms() {
        Realm realm = Realm.getDefaultInstance();

        Observable<RealmResults<ReminderTable>> results = realm.where(ReminderTable.class).findAllSorted("appointmentTime", Sort.DESCENDING).asObservable();


        final Subscription subscription = results.map(new Func1<RealmResults<ReminderTable>, ArrayList<ReminderModel>>() {
            @Override
            public ArrayList<ReminderModel> call(RealmResults<ReminderTable> reminderTables) {
                ArrayList<ReminderModel> reminders = new ArrayList<>();
                for (ReminderTable reminderEntry : reminderTables) {
                    ReminderModel model = new ReminderModel();
                    model.set_id(reminderEntry.get_id());
                    model.setDoctorName(reminderEntry.getDoctorName());
                    model.setPatientName(reminderEntry.getPatientName());
                    model.setPatientNumber(reminderEntry.getPatientNumber());
                    model.setAppointmentTime(reminderEntry.getAppointmentTime());
                    model.setReason(reminderEntry.getReason());

                    reminders.add(model);
                }
                return reminders;
            }
        }).subscribe(new Subscriber<ArrayList<ReminderModel>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ArrayList<ReminderModel> reminderModels) {
                for (ReminderModel reminder : reminderModels) {
                    setAlarm(reminder);
                }

                unsubscribe();
            }

        });

        realm.close();
    }

    private void setSingleAlarm(int reminderID) {
        Realm realm = Realm.getDefaultInstance();
        ReminderTable reminderTable = realm.where(ReminderTable.class).equalTo("_id", reminderID).findFirst();
        if (reminderTable == null) return;

        ReminderModel reminderModel = new ReminderModel();

        reminderModel.set_id(reminderTable.get_id());
        reminderModel.setDoctorName(reminderTable.getDoctorName());
        reminderModel.setPatientName(reminderTable.getPatientName());
        reminderModel.setPatientNumber(reminderTable.getPatientNumber());
        reminderModel.setAppointmentTime(reminderTable.getAppointmentTime());
        reminderModel.setReason(reminderTable.getReason());

        setAlarm(reminderModel);

        realm.close();
    }


    private void setAlarm(ReminderModel reminderModel) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (reminderModel.getAppointmentTime() <= currentTime) {
            return;
        }

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.NOTIFICATION_ID, reminderModel.get_id());
        intent.putExtra(ReminderReceiver.NOTIFICATION_TYPE, ReminderReceiver.SIMPLE_NOTIFICATION);
        PendingIntent pi = PendingIntent.getBroadcast(this, reminderModel.get_id(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(reminderModel.getAppointmentTime());
        int minute = cal.get(Calendar.MINUTE);
        cal.set(Calendar.MINUTE, minute - 1);

        long triggerMillis = cal.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerMillis, pi);
        } else {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pi);
        }
    }

    private void removeAlarm(int reminderID) {
        Realm realm = Realm.getDefaultInstance();
        ReminderTable reminderTable = realm.where(ReminderTable.class).equalTo("_id", reminderID).findFirst();
        if (reminderTable == null) return;

        ReminderModel reminderModel = new ReminderModel();

        reminderModel.set_id(reminderTable.get_id());
        reminderModel.setDoctorName(reminderTable.getDoctorName());
        reminderModel.setPatientName(reminderTable.getPatientName());
        reminderModel.setPatientNumber(reminderTable.getPatientNumber());
        reminderModel.setAppointmentTime(reminderTable.getAppointmentTime());
        reminderModel.setReason(reminderTable.getReason());

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.NOTIFICATION_ID, reminderModel.get_id());
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.cancel(pi);

        realm.close();
    }

}
