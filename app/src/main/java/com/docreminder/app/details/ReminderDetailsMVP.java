package com.docreminder.app.details;

/**
 * <h1>ReminderDetailsMVP </h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */

public class ReminderDetailsMVP {
    public interface ViewOps {
        void showMessage(String message);

        void showInfoDialog(String message);

        void addAlarm(int reminderID);
    }

    public interface PresenterOps {
        void attachView(ViewOps viewOps);

        void detachView();

        void onSaveClick(int reminderID, String doctorName, String patientName, String patientNumber, long timeOfAppointment, String reason);
    }
}
