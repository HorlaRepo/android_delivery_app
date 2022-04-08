package com.connectrail.projects.kwiklink.entities;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 9/26/17.
 */

public class ServiceCategory {

    private String id = "";
    private String name = "";
    private String modeID = "";

    public static final String SERVICE_CATEGORY_TABLE_NAME = "service_categories";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_REMOTE_ID = "remote_id";
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_MODE_ID = "mode_id";

    public static final String CREATE_SERVICE_CATEGORY_TABLE = "create table " + SERVICE_CATEGORY_TABLE_NAME + "(" + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_REMOTE_ID + " integer, " +
            COLUMN_CATEGORY_NAME + " text, " +
            COLUMN_MODE_ID +" integer);";

    public ServiceCategory() {}

    public ServiceCategory(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        name = jsonObject.getString("name");
        modeID = jsonObject.getString("mode_id");
    }
    public ServiceCategory(Cursor cursor) {
        id = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REMOTE_ID)));
        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME));
        modeID = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MODE_ID)));
    }

    public String getId() {
        return id;
    }

    public String getModeID() {
        return modeID;
    }

    public String getName() {
        return name;
    }

    public ContentValues getContentValues() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_REMOTE_ID, id);
        contentValues.put(COLUMN_CATEGORY_NAME, name);
        contentValues.put(COLUMN_MODE_ID, modeID);

        return contentValues;
    }
}
