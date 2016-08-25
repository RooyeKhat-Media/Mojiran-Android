/**
 * © 2016 RooyeKhat Media Co all rights reserved
 * Mojiran Project - Online Stream
 * url : http://rooyekhat.co//
 */

package com.Mojiran.Mojiran.Network;

import android.app.Application;

import java.util.HashMap;


public class GoogleAnalyticsApp extends Application {
    private static final String PROPERTY_ID = "UA-72444338-1";

    public static int GENERAL_TRACKER = 0;


    public enum TrackerName {
        APP_TRACKER, GLOBAL_TRACKER, ECOMMERCE_TRACKER,
    }

    public HashMap mTrackers = new HashMap();


    public GoogleAnalyticsApp() {
        super();
    }


//    @SuppressWarnings("unchecked")
//    public synchronized Tracker getTracker(TrackerName appTracker) {
//        if ( !mTrackers.containsKey(appTracker)) {
//
//            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//
//            Tracker t = (appTracker == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID) : (appTracker == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker) : analytics.newTracker(R.xml.ecommerce_tracker);
//
//            mTrackers.put(appTracker, t);
//
//        }
//
//        return (Tracker) mTrackers.get(appTracker);
//
//    }
}
