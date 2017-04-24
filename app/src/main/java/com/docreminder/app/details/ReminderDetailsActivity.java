package com.docreminder.app.details;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.docreminder.R;
import com.docreminder.db.ReminderTable;
import com.docreminder.dialogs.DateTimeDialog;
import com.docreminder.services.ReminderService;
import com.docreminder.services.RingtoneService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

/**
 * <h1>ReminderDetailsActivity @{@link AppCompatActivity}</h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */
public class ReminderDetailsActivity extends AppCompatActivity implements View.OnClickListener,
        ReminderDetailsMVP.ViewOps, DateTimeDialog.DateSelectedListener {
    public static final String DETAIL_TYPE = "detail_type";
    public static final String REMINDER_ID = "reminder_id";
    public static final String FROM_NOTIFICATION = "from_notification";

    public static final int ADD_ENTRY = 0;
    public static final int VIEW_ENTRY = 1;

    private Toolbar mToolbar;
    private EditText mEtDoctor;
    private EditText mEtPatient;
    private EditText mEtPatientNumber;
    private EditText mEtTime;
    private Button mBtnPickTime;
    private EditText mEtReason;
    private Button mBtnSave;
    private LinearLayout mLlCover;

    private long mAppointmentTime = 0;
    private Snackbar mSnackMessage;

    private ReminderDetailsPresenter mPresenter;
    private DateTimeDialog mTimePickerDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_details);
        initViews();
        initToolbar();
        initSnackbar();
        init();

        mPresenter = new ReminderDetailsPresenter();
        mPresenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);

        int type = getIntent().getIntExtra(DETAIL_TYPE, ADD_ENTRY);
        if (type == ADD_ENTRY) {
            menu.findItem(R.id.menu_details_delete).setVisible(false);
            menu.findItem(R.id.menu_details_edit).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_details_delete:
                int reminderID = getIntent().getIntExtra(REMINDER_ID, -1);
                removeEntry(reminderID);
                break;
            case R.id.menu_details_edit:
                mLlCover.setVisibility(View.GONE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mLlCover = (LinearLayout) findViewById(R.id.ll_cover);
        mEtDoctor = (EditText) findViewById(R.id.et_doctor);
        mEtPatient = (EditText) findViewById(R.id.et_patient);
        mEtPatientNumber = (EditText) findViewById(R.id.et_patient_number);
        mEtTime = (EditText) findViewById(R.id.et_time);
        mBtnPickTime = (Button) findViewById(R.id.btn_pick_time);
        mEtReason = (EditText) findViewById(R.id.et_reason);
        mBtnSave = (Button) findViewById(R.id.btn_save);

        mBtnPickTime.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
    }

    private void init() {
        mTimePickerDialog = new DateTimeDialog(this);
        mTimePickerDialog.setDateSelectedListener(this);

        int type = getIntent().getIntExtra(DETAIL_TYPE, ADD_ENTRY);
        if (type == ADD_ENTRY) {
            mLlCover.setVisibility(View.GONE);
        } else {
            int reminderID = getIntent().getIntExtra(REMINDER_ID, -1);
            viewEntry(reminderID);
        }

        // if user comes from notification then dismiss ringtone if playing
        boolean fromNotification = getIntent().getBooleanExtra(FROM_NOTIFICATION, false);
        int reminderID = getIntent().getIntExtra(REMINDER_ID, -1);
        if (fromNotification) {
            Intent serviceIntent = new Intent(this, RingtoneService.class);
            serviceIntent.putExtra(RingtoneService.TYPE, RingtoneService.TYPE_STOP_RINGTONE);
            serviceIntent.putExtra(RingtoneService.REMINDER_ID, reminderID);
            startService(serviceIntent);
        }
    }

    private void initToolbar() {
        mToolbar.setTitle("Add Reminder");
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFFFF"));
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initSnackbar() {
        mSnackMessage = Snackbar.make(mBtnSave, "", Snackbar.LENGTH_INDEFINITE);
        mSnackMessage.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSnackMessage.dismiss();
            }
        });
    }

    private void viewEntry(int reminderID) {
        Realm realm = Realm.getDefaultInstance();
        ReminderTable table = realm.where(ReminderTable.class).equalTo("_id", reminderID).findFirst();
        if (table == null) {
            realm.close();
            return;
        }
        mEtDoctor.setText(table.getDoctorName());
        mEtPatient.setText(table.getPatientName());
        mEtPatientNumber.setText(table.getPatientName());
        onDateSelected(table.getAppointmentTime());
        mEtReason.setText(table.getReason());

        realm.close();
    }

    private void removeEntry(int reminderId) {
        Realm realm = Realm.getDefaultInstance();
        ReminderTable table = realm.where(ReminderTable.class).equalTo("_id", reminderId).findFirst();
        if (table == null) {
            realm.close();
            return;
        }
        realm.beginTransaction();
        table.deleteFromRealm();
        realm.commitTransaction();

        Intent intent = new Intent(this, ReminderService.class);
        intent.putExtra(ReminderService.INTENT_TASK, ReminderService.TASK_REMOVE_ALARM);
        intent.putExtra(ReminderService.INTENT_REMINDER_ID, reminderId);
        startService(intent);

        realm.close();
        onBackPressed();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_save) {
            String doctorName = mEtDoctor.getText().toString();
            String patientName = mEtPatient.getText().toString();
            String patientNumber = mEtPatientNumber.getText().toString();
            long appointmentTime = mAppointmentTime;
            String reason = mEtReason.getText().toString();
            int reminderID = getIntent().getIntExtra(REMINDER_ID, -1);
            mPresenter.onSaveClick(reminderID, doctorName, patientName, patientNumber, appointmentTime, reason);
        } else if (id == R.id.btn_pick_time) {
            mTimePickerDialog.show();
        }
    }

    @Override
    public void showMessage(String message) {
        mSnackMessage.setText(message).show();
    }

    @Override
    public void showInfoDialog(String message) {
        new AlertDialog.Builder(this).
                setMessage(message)
                .setNeutralButton("Ok", new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                }).show();
    }

    @Override
    public void addAlarm(int reminderID) {
        Intent intent = new Intent(this, ReminderService.class);
        intent.putExtra(ReminderService.INTENT_TASK, ReminderService.TASK_ADD_SINGLE_ALARM);
        intent.putExtra(ReminderService.INTENT_REMINDER_ID, reminderID);
        startService(intent);
    }

    @Override
    public void onDateSelected(long timeInMillis) {
        mAppointmentTime = timeInMillis;

        Date date = new Date(timeInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(date);

        mEtTime.setText(formattedDate);
    }
}
