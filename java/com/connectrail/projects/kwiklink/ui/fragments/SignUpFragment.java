package com.connectrail.projects.kwiklink.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.connectrail.projects.kwiklink.R;
import com.connectrail.projects.kwiklink.core.Requests;
import com.connectrail.projects.kwiklink.ui.activities.AuthenticationActivity;
import com.connectrail.projects.kwiklink.ui.base.BaseFragment;
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

public class SignUpFragment extends BaseFragment {

    @BindView(R.id.edt_first_name_sign_up)
    MaterialEditText firstNameEditText;
    @BindView(R.id.edt_last_name_sign_up)
    MaterialEditText lastNameEditText;
    @BindView(R.id.edt_email_sign_up)
    MaterialEditText emailEditText;
    @BindView(R.id.edt_password_sign_up)
    MaterialEditText passwordEditText;
    @BindView(R.id.loading_layout_sign_up)
    RelativeLayout loadingLayout;

    private String phoneNumber = "";

    public static SignUpFragment newInstance(String phone) {

        Bundle args = new Bundle();
        args.putString("phone_number", phone);
        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneNumber = getArguments().getString("phone_number");

    }
    @OnClick(R.id.btn_sign_up) public void onSignUpClick() {

        if(Util.empty(firstNameEditText)) {
            firstNameEditText.setError("Enter first name");
            return;
        }
        if(Util.empty(lastNameEditText)) {
            lastNameEditText.setError("Enter last name");
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(Util.text(emailEditText)).matches()) {
            emailEditText.setError("Invalid email address.");
            return;
        }
        if(Util.text(passwordEditText).length() < 4) {
            passwordEditText.setError("Enter your password. Password too short");
            return;
        }
        Util.hideKeyboard(getActivity());
        RequestParams requestParams = new RequestParams();
        requestParams.put("firstname", Util.text(firstNameEditText));
        requestParams.put("lastname", Util.text(lastNameEditText));
        requestParams.put("email", Util.text(emailEditText));
        requestParams.put("password", Util.text(passwordEditText));
        requestParams.put("phone", phoneNumber);

        Requests.post("/user/signup", requestParams, textHttpResponseHandler);
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            showDialog("Error", "Error response from network");
            L.WTF(responseString, throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            L.fine(responseString);
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                if(!jsonObject.getBoolean("error")) {
                    //start OTP
                    AuthenticationActivity.swap(OtpVerificationFragment.newInstance(phoneNumber));
                }else {
                    showDialog("Error", "Error response from network");
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
