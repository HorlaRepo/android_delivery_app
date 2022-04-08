package com.connectrail.projects.kwiklink.core.local;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.connectrail.projects.kwiklink.MainApplication;
import com.connectrail.projects.kwiklink.entities.ServiceCategory;
import com.connectrail.projects.kwiklink.entities.ServiceMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 9/26/17.
 */

public class DatabaseManager {

    private DatabaseHelper databaseHelper;
    private static DatabaseManager instance;

    private DatabaseManager() {
        databaseHelper = new DatabaseHelper(
                MainApplication.getInstance().getApplicationContext()
        );
    }
    public static synchronized DatabaseManager getInstance() {
        if(instance == null) instance = new DatabaseManager();
        return instance;
    }

    public DatabaseHelper getHelper() {
        return databaseHelper;
    }
    public SQLiteDatabase getDatabase() {
        return getHelper().getWritableDatabase();
    }
    public List<ServiceCategory> serviceCategoryFor(double distance) {

        String query = "SELECT * FROM " + ServiceMode.TABLE_SERVICE_MODE + " WHERE " +
                ServiceMode.COLUMN_DISTANCE + " <= ?";

        long round = Math.round(distance);
        Cursor cursor = getDatabase().rawQuery(query, new String[]{String.valueOf(round)});
        if(cursor == null)
            return new ArrayList<>();

        if(cursor.getCount() <= 0 || !cursor.moveToFirst())
            return new ArrayList<>();

        List<ServiceCategory> serviceCategories = new ArrayList<>();
        List<ServiceCategory> all = all();

        List<ServiceMode> serviceModes = new ArrayList<>();
        while (cursor.moveToNext()) {
            ServiceMode serviceMode = new ServiceMode(cursor);
            serviceModes.add(serviceMode);
        }

        for (ServiceMode serviceMode : serviceModes) {
            for (ServiceCategory serviceCategory : all) {
                if(serviceMode.getId().equalsIgnoreCase(serviceCategory.getModeID())) {
                    serviceCategories.add(serviceCategory);
                }
            }
        }

        cursor.close();
        return serviceCategories;
    }
    public List<ServiceCategory> all() {

        Cursor cursor = getDatabase().query(ServiceCategory.SERVICE_CATEGORY_TABLE_NAME, null, null, null, null, null, null);
        if(cursor == null)
            return new ArrayList<>();

        List<ServiceCategory> all = new ArrayList<>();
        if(cursor.getCount() <= 0 || !cursor.moveToFirst())
            return new ArrayList<>();

        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            ServiceCategory serviceCategory = new ServiceCategory(cursor);
            all.add(serviceCategory);
        }
        cursor.close();
        return all;
    }
    public boolean hasServiceCategory() {
        return all().size() > 0;
    }
    public void putServiceCategory(ServiceCategory serviceCategory) {

        if(!hasServiceCategory(serviceCategory)) {
            getDatabase().insert(ServiceCategory.SERVICE_CATEGORY_TABLE_NAME, null, serviceCategory.getContentValues());
        }
    }

    public boolean hasServiceCategory(ServiceCategory serviceCategory) {
        String where = ServiceCategory.COLUMN_REMOTE_ID + "=?";
        String whereArgs[] = {serviceCategory.getId()};

        Cursor cursor = getDatabase().query(ServiceCategory.SERVICE_CATEGORY_TABLE_NAME,
                null, where, whereArgs, null, null, null);
        if(cursor == null)
            return false;

        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }
    public void putServiceMode(ServiceMode serviceMode) {
        if(!hasServiceMode(serviceMode)) {
            getDatabase().insert(ServiceMode.TABLE_SERVICE_MODE, null, serviceMode.getContentValues());
        }
    }
    public boolean hasServiceMode(ServiceMode mode) {

        String where = ServiceMode.COLUMN_REMOTE_ID + "=?";
        String whereArgs[] = {mode.getId()};

        Cursor cursor = getDatabase().query(ServiceMode.TABLE_SERVICE_MODE, null, where, whereArgs, null, null, null);
        if(cursor == null)
            return false;

        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }
    public ServiceMode getServiceMode(String id) {
        String where = ServiceMode.COLUMN_REMOTE_ID + "=?";
        String whereArgs[] = {id};

        Cursor cursor = getDatabase().query(ServiceMode.TABLE_SERVICE_MODE, null, where, whereArgs, null, null, null);
        if(cursor == null)
            return new ServiceMode();

        cursor.moveToFirst();
        ServiceMode serviceMode = new ServiceMode(cursor);

        cursor.close();
        return serviceMode;
    }
    public List<ServiceMode> allServiceModes() {

        Cursor cursor = getDatabase().query(ServiceMode.TABLE_SERVICE_MODE, null, null, null, null, null, null);
        if(cursor == null)
            return new ArrayList<>();

        List<ServiceMode> all = new ArrayList<>();
        if(cursor.getCount() <= 0 || !cursor.moveToFirst())
            return new ArrayList<>();

        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            ServiceMode serviceMode = new ServiceMode(cursor);
            all.add(serviceMode);
        }
        cursor.close();
        return all;
    }
}