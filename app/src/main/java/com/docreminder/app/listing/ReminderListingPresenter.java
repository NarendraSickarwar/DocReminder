package com.docreminder.app.listing;

import com.docreminder.app.listing.ReminderListingMVP.PresenterOps;
import com.docreminder.db.ReminderTable;
import com.docreminder.models.ReminderModel;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * <h1>ReminderListingPresenter  </h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */


public class ReminderListingPresenter implements PresenterOps {
    private ReminderListingMVP.ViewOps mViewOps;
    private Realm mRealm;
    private CompositeSubscription mSubscription;

    public ReminderListingPresenter() {
        mSubscription = new CompositeSubscription();
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void getReminders() {
        Observable<RealmResults<ReminderTable>> results = mRealm.where(ReminderTable.class).findAllSorted("appointmentTime", Sort.DESCENDING).asObservable();


        Subscription subscription = results.map(new Func1<RealmResults<ReminderTable>, ArrayList<ReminderModel>>() {
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
                if (reminderModels.size() == 0) {
                    mViewOps.showEmptyView();
                } else {
                    mViewOps.hideEmptyView();
                }
                mViewOps.showAppointmentListing(reminderModels);
            }
        });

        mSubscription.add(subscription);

    }

    @Override
    public void attachView(ReminderListingMVP.ViewOps viewOps) {
        mViewOps = viewOps;
    }

    @Override
    public void detachView() {
        mViewOps = null;
        mRealm.close();
        mSubscription.unsubscribe();
    }
}
