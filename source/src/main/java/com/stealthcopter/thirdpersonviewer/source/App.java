package com.stealthcopter.thirdpersonviewer.source;

import android.app.Application;

import com.stealthcopter.thirdpersonviewer.source.BuildConfig;

import timber.log.Timber;

/**
 * Created by mat on 25/11/16.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            //Timber.plant(new CrashReportingTree());
        }
    }

}
