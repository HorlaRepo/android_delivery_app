package com.connectrail.projects.kwiklink.core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.connectrail.projects.kwiklink.MainApplication;
import com.connectrail.projects.kwiklink.entities.User;
import com.connectrail.projects.kwiklink.util.L;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 9/25/17.
 */

public class MemoryManager {

    private static MemoryManager sInstance;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;

    private MemoryManager() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                MainApplication.getInstance().getApplicationContext()
        );
        editor = mSharedPreferences.edit();
    }
    public static synchronized MemoryManager getInstance() {
        if(sInstance == null)  sInstance = new MemoryManager();
        return sInstance;
    }
    public void saveLastKnownLocation(String name, double latitude, double longitude) {
        editor.putString("location_name", name)
                .putString("latitude", String.valueOf(latitude))
                .putString("longitude", String.valueOf(longitude))
                .apply();
    }
    public String getLastKnownLocationName() {
        return mSharedPreferences.getString("location_name", "");
    }
    public double getLastKnownLocationLatitude() {

        String latitude = mSharedPreferences.getString("latitude", "");
        if(latitude.isEmpty())
            return 0;

        return Double.valueOf(latitude);
    }
    public double getLastKnownLocationLongitude() {

        String longitude = mSharedPreferences.getString("longitude", "");
        if(longitude.isEmpty())
            return 0;

        return Double.valueOf(longitude);
    }
    public void putUser(User user) {
        editor.putString("user_", user.getRaw()).apply();
    }
    public User getUser() {
        try {
            return new User(new JSONObject(mSharedPreferences.getString("user_", "{}")));
        }catch (JSONException e) {
            L.WTF(e);
        }
        return new User();
    }
    public boolean hasSession() {
        User user = getUser();
        return !user.getUserID().isEmpty() || !user.getPhoneNumber().isEmpty();
    }
}
