package com.connectrail.projects.kwiklink.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.connectrail.projects.kwiklink.R;
import com.connectrail.projects.kwiklink.core.Requests;
import com.connectrail.projects.kwiklink.ui.activities.AuthenticationActivity;
import com.connectrail.projects.kwiklink.ui.base.BaseFragment;
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

public class LoginFragment extends BaseFragment {


    @BindView(R.id.edt_password_login_activity)
    MaterialEditText passwordEditText;
    @BindView(R.id.loading_layout_login)
    RelativeLayout loadingLayout;
    @BindView(R.id.tv_username_login)
    TextView usernameTextView;

    private String phoneNumber = "";


    public static LoginFragment newInstance(String username, String phone) {

        Bundle args = new Bundle();
        args.putString("current_username", username);
        args.putString("phone_number", phone);

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String text = "Welcome, " + getArguments().getString("current_username");
        usernameTextView.setText(text);

        phoneNumber = getArguments().getString("phone_number");
    }
    @OnClick(R.id.btn_sign_in_login_activity) public void onSignInClick() {

        if(Util.empty(passwordEditText)) {
            passwordEditText.setError("Invalid password");
            return;
        }
        Util.hideKeyboard(getActivity());

        RequestParams requestParams = new RequestParams();
        requestParams.put("phone", phoneNumber);
        requestParams.put("password", Util.text(passwordEditText));

        Requests.post("/user/login", requestParams, textHttpResponseHandler);
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            toast("Error response from remote server.");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                if(!jsonObject.getBoolean("error")) {
                    //start OTP
                    AuthenticationActivity.swap(OtpVerificationFragment.newInstance(phoneNumber));
                }else {
                    showDialog("Authentication Error", "Invalid credentials. Please retry");
                }
            }catch (JSONException e) {

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
