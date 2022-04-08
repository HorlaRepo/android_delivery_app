package com.connectrail.projects.kwiklink.ui.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.connectrail.projects.kwiklink.R;
import com.connectrail.projects.kwiklink.core.Requests;
import com.connectrail.projects.kwiklink.ui.base.BaseActivity;
import com.connectrail.projects.kwiklink.util.L;
import com.connectrail.projects.kwiklink.util.Util;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by root on 9/25/17.
 */

public class VerifyPhoneActivity extends BaseActivity {

    @BindView(R.id.edt_phone_number_auth_activity)
    MaterialEditText phoneNumberEditText;
    @BindView(R.id.loading_layout_auth)
    RelativeLayout loadingLayout;

    private String phoneNumber = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_authenticate);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
    @OnClick(R.id.btn_continue_auth_activity) public void onContinueClick() {

        if(Util.text(phoneNumberEditText).length() < 10 || Util.text(phoneNumberEditText).length() > 11) {
            phoneNumberEditText.setError("Invalid mobile number.");
            return;
        }
        phoneNumber = Util.text(phoneNumberEditText);
        RequestParams requestParams = new RequestParams();
        requestParams.put("phone", Util.text(phoneNumberEditText));

        Util.hideKeyboard(this);
        Requests.post("/user/authenticate", requestParams, textHttpResponseHandler);
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            toast("Error response from remote server. Please retry");
            L.WTF(responseString, throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            L.fine(responseString);
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                if(jsonObject.getBoolean("authenticated")) {
                    AuthenticationActivity.startLogin(VerifyPhoneActivity.this, jsonObject.getString("fullname"), phoneNumber);
                }else {
                    AuthenticationActivity.startSignUp(VerifyPhoneActivity.this, phoneNumber);
                }
            }catch (JSONException e) {
                L.WTF(e);
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            loadingLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            loadingLayout.setVisibility(View.GONE);
        }
    };
}
