package com.docreminder.app.listing;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.docreminder.R;
import com.docreminder.app.details.ReminderDetailsActivity;
import com.docreminder.models.ReminderModel;

import java.util.ArrayList;

import rx.Subscription;

/**
 * <h1>ReminderListingActivity  @{@link AppCompatActivity}</h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */

public class ReminderListingActivity extends AppCompatActivity implements View.OnClickListener,
        ReminderListingMVP.ViewOps, ReminderListingAdapter.ItemClickListener {
    private Toolbar mToolbar;
    private RecyclerView mRvAppointments;
    private LinearLayout mLlEmptyView;
    private FloatingActionButton mFabAddAppointment;

    private ReminderListingAdapter mReminderAdapter;
    private Snackbar mSnackMessage;

    private ReminderListingPresenter mPresenter;
    private Subscription mTimerSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_listing);
        initViews();
        initToolbar();
        initSnackbar();

        mPresenter = new ReminderListingPresenter();
        mPresenter.attachView(this);
        mPresenter.getReminders();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRvAppointments = (RecyclerView) findViewById(R.id.rv_appointments);
        mLlEmptyView = (LinearLayout) findViewById(R.id.ll_empty_view);
        mFabAddAppointment = (FloatingActionButton) findViewById(R.id.fab_add_appointment);
        mFabAddAppointment.setOnClickListener(this);

        mReminderAdapter = new ReminderListingAdapter();
        mReminderAdapter.setItemClickListener(this);
        mRvAppointments.setLayoutManager(new LinearLayoutManager(this));
        mRvAppointments.setAdapter(mReminderAdapter);
    }

    private void initToolbar() {
        mToolbar.setTitle("Reminders");
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFFFF"));
        setSupportActionBar(mToolbar);
    }


    private void initSnackbar() {
        mSnackMessage = Snackbar.make(mToolbar, "", Snackbar.LENGTH_INDEFINITE);
        mSnackMessage.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSnackMessage.dismiss();
            }
        });
    }

    @Override
    public void showMessage(String message) {
        mSnackMessage.setText(message);
        mSnackMessage.show();
    }

    @Override
    public void showEmptyView() {
        mLlEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyView() {
        mLlEmptyView.setVisibility(View.GONE);
    }

    @Override
    public void showAppointmentListing(ArrayList<ReminderModel> reminders) {
        mReminderAdapter.setData(reminders);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fab_add_appointment) {
            Intent intent = new Intent(this, ReminderDetailsActivity.class);
            intent.putExtra(ReminderDetailsActivity.DETAIL_TYPE, ReminderDetailsActivity.ADD_ENTRY);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(ReminderModel reminderModel) {
        Intent detailIntent = new Intent(this, ReminderDetailsActivity.class);
        detailIntent.putExtra(ReminderDetailsActivity.DETAIL_TYPE, ReminderDetailsActivity.VIEW_ENTRY);
        detailIntent.putExtra(ReminderDetailsActivity.REMINDER_ID, reminderModel.get_id());
        startActivity(detailIntent);
    }
}
