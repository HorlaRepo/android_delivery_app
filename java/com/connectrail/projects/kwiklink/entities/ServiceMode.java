package com.connectrail.projects.kwiklink.entities;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 9/26/17.
 */

public class ServiceMode {

    private String id = "";
    private String price = "";
    private String distance = "";
    private String name = "";

    public static final String TABLE_SERVICE_MODE = "service_mode";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_REMOTE_ID = "remote_id";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_NAME = "name";

    public static final String CREATE_SERVICE_MODE_TABLE = "create table " + TABLE_SERVICE_MODE + "(" + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_REMOTE_ID + " integer, " +
            COLUMN_PRICE + " varchar(100), " +
            COLUMN_DISTANCE + " text, " + COLUMN_NAME + " text);";

    public ServiceMode() {}

    public ServiceMode(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        price = jsonObject.getString("price");
        distance = jsonObject.getString("distance");
        name = jsonObject.getString("name");
    }
    public ServiceMode(Cursor cursor) {
        id = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REMOTE_ID)));
        price = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
        distance = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISTANCE));
        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDistance() {
        return distance;
    }

    public String getPrice() {
        return price;
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_REMOTE_ID, id);
        contentValues.put(COLUMN_PRICE, price);
        contentValues.put(COLUMN_DISTANCE, distance);
        contentValues.put(COLUMN_NAME, name);

        return contentValues;
    }
}
