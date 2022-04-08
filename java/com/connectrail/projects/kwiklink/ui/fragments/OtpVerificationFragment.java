package com.connectrail.projects.kwiklink.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.connectrail.projects.kwiklink.R;
import com.connectrail.projects.kwiklink.core.MemoryManager;
import com.connectrail.projects.kwiklink.core.Requests;
import com.connectrail.projects.kwiklink.entities.User;
import com.connectrail.projects.kwiklink.ui.activities.MainActivity;
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

public class OtpVerificationFragment extends BaseFragment {

    @BindView(R.id.edt_code_verify_otp)
    MaterialEditText codeEditText;
    @BindView(R.id.loading_layout_verify_otp)
    RelativeLayout loadingLayout;

    private String phoneNumber = "";


    public static OtpVerificationFragment newInstance(String phoneNumber) {

        Bundle args = new Bundle();
        args.putString("phone_number", phoneNumber);

        OtpVerificationFragment fragment = new OtpVerificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_verify_otp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneNumber = getArguments().getString("phone_number");
    }
    @OnClick(R.id.btn_verify_otp) public void onVerifyClick() {

        if(Util.text(codeEditText).length() < 4) {
            codeEditText.setError("Invalid Otp. Otp length must be equal to 4");
            return;
        }
        int code = -1;
        try {
            code = Integer.parseInt(Util.text(codeEditText));
        }catch (NumberFormatException ex) {
            codeEditText.setError("Invalid otp");
        }
        if(code == -1)
            return;

        RequestParams requestParams = new RequestParams();
        requestParams.put("phone", phoneNumber);
        requestParams.put("otp", Util.text(codeEditText));

        Requests.post("/user/validate/otp", requestParams, textHttpResponseHandler);
    }
    private TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            toast("Error response from server. Please retry");
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            L.fine(responseString);
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                if(!jsonObject.getBoolean("error")) {
                    User user = new User(jsonObject.getJSONObject("user"));
                    MemoryManager.getInstance().putUser(user);

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    showDialog("Authentication Error", "Invalid otp!");
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

