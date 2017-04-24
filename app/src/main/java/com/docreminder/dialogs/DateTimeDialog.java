package com.docreminder.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.docreminder.R;

import java.util.Calendar;

/**
 * <h1>DateTimeDialog  @{@link Dialog} </h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */


public class DateTimeDialog extends Dialog implements View.OnClickListener {
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private Button mBtnPick;

    private DateSelectedListener mListener;

    public DateTimeDialog(Context context) {
        super(context);
    }

    public void setDateSelectedListener(DateSelectedListener listener) {
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.dialog_time_picker);
        initViews();
        init();
    }

    private void initViews() {
        mDatePicker = (DatePicker) findViewById(R.id.date_picker);
        mTimePicker = (TimePicker) findViewById(R.id.time_picker);
        mBtnPick = (Button) findViewById(R.id.btn_date_time_set);

        mBtnPick.setOnClickListener(this);
    }

    private void init() {
        Calendar calendar = Calendar.getInstance();
        //mDatePicker.setMinDate(calendar.getTimeInMillis());
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnPick) {
            int year = mDatePicker.getYear();
            int month = mDatePicker.getMonth();
            int day = mDatePicker.getDayOfMonth();
            int hour = mTimePicker.getCurrentHour();
            int minute = mTimePicker.getCurrentMinute();

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);

            if (mListener != null) {
                mListener.onDateSelected(cal.getTimeInMillis());
            }

            dismiss();
        }
    }

    public interface DateSelectedListener {
        void onDateSelected(long timeInMillis);
    }
}
