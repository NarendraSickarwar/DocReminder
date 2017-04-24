package com.docreminder.app.details;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.docreminder.app.details.ReminderDetailsMVP.PresenterOps;
import com.docreminder.db.ReminderTable;

import java.util.Calendar;

import io.realm.Realm;

/**
 * <h1>ReminderDetailsPresenter </h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */


public class ReminderDetailsPresenter implements PresenterOps {
    private ReminderDetailsMVP.ViewOps mViewOps;

    public ReminderDetailsPresenter() {
    }

    @Override
    public void attachView(@NonNull ReminderDetailsMVP.ViewOps viewOps) {
        mViewOps = viewOps;
    }

    @Override
    public void detachView() {
        mViewOps = null;
    }

    @Override
    public void onSaveClick(int reminderID, String doctorName, String patientName, String patientNumber, long timeOfAppointment, String reason) {
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();

        if (TextUtils.isEmpty(doctorName)) {
            mViewOps.showMessage("Doctor name is empty");
            return;
        } else if (TextUtils.isEmpty(patientName)) {
            mViewOps.showMessage("Patient Name is emtpy");
            return;
        } else if (TextUtils.isEmpty(patientNumber)) {
            mViewOps.showMessage("Patient Number is empty");
            return;
        } else if (timeOfAppointment == 0) {
            mViewOps.showMessage("Choose appointment time");
            return;
        } else if (currentTime >= timeOfAppointment) {
            mViewOps.showMessage("Choose appropriate appointment time");
            return;
        } else if (TextUtils.isEmpty(reason)) {
            mViewOps.showMessage("Reason is empty");
            return;
        }

        Realm realm = Realm.getDefaultInstance();

        // incrementing primary key because realm doesn't support primary key autoincrement
        int primaryKey = 0;
        Number currentMaxID = realm.where(ReminderTable.class).max("_id");
        if (currentMaxID != null) {
            primaryKey = currentMaxID.intValue() + 1;
        }

        if (reminderID != -1) {
            primaryKey = reminderID;
        }

        ReminderTable reminderDetails = new ReminderTable();
        reminderDetails.set_id(primaryKey);
        reminderDetails.setDoctorName(doctorName);
        reminderDetails.setPatientName(patientName);
        reminderDetails.setPatientNumber(patientNumber);
        reminderDetails.setAppointmentTime(timeOfAppointment);
        reminderDetails.setReason(reason);

        // inserting entry in realm database
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(reminderDetails);
        //realm.insertOrUpdate(reminderDetails);
        realm.commitTransaction();

        realm.close();

        mViewOps.showInfoDialog("Appointment saved successfully");
        mViewOps.addAlarm(primaryKey);

    }
}
