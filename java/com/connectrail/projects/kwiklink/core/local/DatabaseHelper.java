package com.connectrail.projects.kwiklink.core.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.connectrail.projects.kwiklink.entities.ServiceCategory;
import com.connectrail.projects.kwiklink.entities.ServiceMode;

/**
 * Created by root on 9/26/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "kwiki______";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ServiceMode.CREATE_SERVICE_MODE_TABLE);
        sqLiteDatabase.execSQL(ServiceCategory.CREATE_SERVICE_CATEGORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i != i1) {
            sqLiteDatabase.execSQL("DROP TABLE " + ServiceCategory.SERVICE_CATEGORY_TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE " + ServiceMode.TABLE_SERVICE_MODE);

            sqLiteDatabase.execSQL(ServiceMode.CREATE_SERVICE_MODE_TABLE);
            sqLiteDatabase.execSQL(ServiceCategory.CREATE_SERVICE_CATEGORY_TABLE);
        }
    }
}
