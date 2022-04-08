package com.connectrail.projects.kwiklink;

import android.app.Application;

import com.connectrail.projects.kwiklink.core.Requests;
import com.connectrail.projects.kwiklink.core.local.DatabaseManager;
import com.connectrail.projects.kwiklink.entities.ServiceCategory;
import com.connectrail.projects.kwiklink.entities.ServiceMode;
import com.connectrail.projects.kwiklink.util.L;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by root on 9/24/17.
 */

public class MainApplication extends Application {

    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private static MainApplication instance;

    private DatabaseManager databaseManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("Lato-Light.ttf")
                .setFontAttrId(R.attr.fontPath).build());

        databaseManager = DatabaseManager.getInstance();
        if(databaseManager.all().size() <= 0) {
            Requests.get("/service/category", serviceCategoryHttpResponseHandler );
        }
        if(databaseManager.allServiceModes().size() <= 0) {
            Requests.get("/service/mode", serviceModeHttpResponseHandler);
        }
    }
    private TextHttpResponseHandler serviceCategoryHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            L.fine(responseString);
            try {
                JSONArray jsonArray = new JSONObject(responseString).getJSONArray("service_category");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ServiceCategory serviceCategory = new ServiceCategory(jsonArray.getJSONObject(i));
                    databaseManager.putServiceCategory(serviceCategory);
                }
            }catch (JSONException e) {
                L.WTF(e);
            }
        }
    };
    private TextHttpResponseHandler serviceModeHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            try {
                JSONArray jsonArray = new JSONObject(responseString).getJSONArray("service_mode");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ServiceMode serviceMode = new ServiceMode(jsonArray.getJSONObject(i));
                    databaseManager.putServiceMode(serviceMode);
                }
            }catch (JSONException e) {
                L.WTF(e);
            }
        }
    };

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static MainApplication getInstance() {
        return instance;
    }
}
