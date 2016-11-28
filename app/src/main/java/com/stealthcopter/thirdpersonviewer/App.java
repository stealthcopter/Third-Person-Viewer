package com.stealthcopter.thirdpersonviewer;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by mat on 25/11/16.
 */

public class App extends Application {

    /*
    // TODO: Automatic network discovery

    // TODO: Show message when nothing sent / found

    // TODO: Cardboard icon
    // TODO: VR View button

    // TODO: Switch to sending video
    // TODO: Switching cameras
    // TODO: Reconnecting to camera on disconnect event
     */

    @Override
    public void onCreate() {
        super.onCreate();

        initLogging();
        initCrashHandling();
    }

    private void initCrashHandling() {
        Fabric.with(this, new Crashlytics());
    }

    private void initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            Crashlytics.log(priority, tag, message);
            if (t != null) {
                Crashlytics.logException(t);
            }
        }
    }

}
