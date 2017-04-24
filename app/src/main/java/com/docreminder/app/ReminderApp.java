package com.docreminder.app;

import android.app.Application;

import io.realm.Realm;

/**
 * <h1>ReminderApp  @{@link Application} </h1>
 *
 * @author Narendra Singh
 * @version 1.0
 * @since 24-04-2017
 */


public class ReminderApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(getApplicationContext());
    }
}
