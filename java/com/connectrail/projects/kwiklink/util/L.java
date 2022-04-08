package com.connectrail.projects.kwiklink.util;

import android.util.Log;

/**
 * Created by root on 9/25/17.
 */

public class L {

    public static final String TAG = "KwikLink";

    public static void WTF(String data, Throwable throwable) {
        Log.d(TAG, data + "_", throwable);
    }
    public static void WTF(Throwable throwable) {
        WTF("ERROR", throwable);
    }
    public static void fine(String data) {
        Log.d(TAG, data + "_");
    }
}
