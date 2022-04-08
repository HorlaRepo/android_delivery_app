package com.connectrail.projects.kwiklink.core;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * 6/13/2017.
 */

public final class Requests {

    public static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public static String BASE_URL = "http://192.168.1.92/kwikie/app/v1";


    public static final String AUTH = "Authorization";

    public static void post(String endPoint, RequestParams requestParams, TextHttpResponseHandler textHttpResponseHandler) {

        asyncHttpClient.post(BASE_URL + endPoint, requestParams, textHttpResponseHandler);

    }
    public static void get(String endPoint, RequestParams requestParams, TextHttpResponseHandler responseHandler) {

        asyncHttpClient.get(BASE_URL + endPoint, requestParams, responseHandler);
    }
    public static void get(String endPoint, TextHttpResponseHandler textHttpResponseHandler) {
        get(endPoint, null, textHttpResponseHandler);
    }
    public static void put(String endPoint, RequestParams requestParams, TextHttpResponseHandler textHttpResponseHandler) {

        asyncHttpClient.put(BASE_URL + endPoint, requestParams, textHttpResponseHandler);
    }
}
