package com.connectrail.projects.kwiklink.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.connectrail.projects.kwiklink.R;
import com.connectrail.projects.kwiklink.ui.base.BaseActivity;
import com.connectrail.projects.kwiklink.ui.fragments.LoginFragment;
import com.connectrail.projects.kwiklink.ui.fragments.SignUpFragment;

/**
 * Created by root on 9/25/17.
 */

public class AuthenticationActivity extends BaseActivity {

    private static FragmentManager fragmentManager;
    public static final String ACTION_AUTH_TYPE = "com.connectrail.projects.kwilink.ACTION_AUTH_TYPE";

    public static void startLogin(Context context, String username, String phoneNumber) {
        Intent in = new Intent(context, AuthenticationActivity.class);
        in.putExtra(ACTION_AUTH_TYPE, "login");
        in.putExtra("current_username", username);
        in.putExtra("phone_number", phoneNumber);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(in);
    }
    public static void startSignUp(Context context, String phoneNumber) {
        Intent in = new Intent(context, AuthenticationActivity.class);
        in.putExtra(ACTION_AUTH_TYPE, "signUp");
        in.putExtra("phone_number", phoneNumber);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(in);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_authentication_activity);

        Intent intent = getIntent();
        if(intent == null) {
            finish();return;
        }
        fragmentManager = getSupportFragmentManager();
        boolean isLogin = intent.getStringExtra(ACTION_AUTH_TYPE).equals("login");
        if(isLogin) {
            String username = intent.getStringExtra("current_username");
            String phone = intent.getStringExtra("phone_number");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout_auth_activity, LoginFragment.newInstance(username, phone), LoginFragment.class.getSimpleName())
                    .commit();
        }else {
            String phone = intent.getStringExtra("phone_number");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout_auth_activity, SignUpFragment.newInstance(phone), SignUpFragment.class.getSimpleName())
                    .commit();
        }
    }
    public static void swap(Fragment fragment) {

        fragmentManager.beginTransaction().replace(R.id.frame_layout_auth_activity,
                fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragmentManager = null;
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle("Quit App")
                .setMessage("Quit Kwikie")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton("NO", null).create().show();


        super.onBackPressed();
    }
}
