package com.docreminder.app.listing;

import com.docreminder.models.ReminderModel;

import java.util.ArrayList;

/**
 * <h1>ReminderListingMVP  </h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */


public class ReminderListingMVP {
    public interface ViewOps {
        void showMessage(String message);

        void showEmptyView();

        void hideEmptyView();

        void showAppointmentListing(ArrayList<ReminderModel> reminders);
    }

    public interface PresenterOps {
        void getReminders();

        void attachView(ReminderListingMVP.ViewOps viewOps);

        void detachView();
    }
}
