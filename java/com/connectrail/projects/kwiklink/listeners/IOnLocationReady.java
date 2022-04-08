package com.connectrail.projects.kwiklink.listeners;

import android.location.Address;

/**
 * Created by root on 9/24/17.
 */

public interface IOnLocationReady {

    void onReady(Address address);
    void onError(Throwable throwable);
}
