package com.udacity.project.reddit.capstone.utils;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.udacity.project.reddit.capstone.R;

/**
 * Created by Neha on 25-10-2017.
 */

public class Apptil extends Application {
    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    public static GoogleAnalytics analytics() {
        return analytics;
    }

    public static Tracker tracker() {
        return tracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        analytics = GoogleAnalytics.getInstance(this);

        // TODO: Replace the tracker-id with your app one from https://www.google.com/analytics/web/
        tracker = analytics.newTracker(R.xml.global_tracker);

        // Provide unhandled exceptions reports. Do that first after creating the tracker
        tracker.enableExceptionReporting(true);

        // Enable Remarketing, Demographics & Interests reports
        // https://developers.google.com/analytics/devguides/collection/android/display-features
        tracker.enableAdvertisingIdCollection(true);

        // Enable automatic activity tracking for your app
        tracker.enableAutoActivityTracking(true);
    }
}
