package com.connectrail.projects.kwiklink.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 9/25/17.
 */

public class User {

    private String userID = "";
    private String firstname = "";
    private String lastName = "";
    private String email = "";
    private String phoneNumber = "";
    private String timeReg = "";
    private String fcmToken = "";

    private String raw = "";

    public User() {}

    public User(JSONObject jsonObject) throws JSONException {
        userID = jsonObject.getString("id");
        firstname = jsonObject.getString("firstname");
        lastName = jsonObject.getString("lastname");
        email = jsonObject.getString("email");
        phoneNumber = jsonObject.getString("phone");
        timeReg = jsonObject.getString("time_reg");
        fcmToken = jsonObject.getString("fcm_token");

        raw = jsonObject.toString();
    }

    public String getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTimeReg() {
        return timeReg;
    }

    public String getRaw() {
        return raw;
    }
    public String getFullname() {
        return firstname.toUpperCase() + ", " + lastName;
    }
}
