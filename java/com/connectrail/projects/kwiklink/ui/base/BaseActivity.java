package com.connectrail.projects.kwiklink.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.connectrail.projects.kwiklink.R;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by root on 9/24/17.
 */

public class BaseActivity extends AppCompatActivity {

    private volatile boolean isOn = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOn = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOn = true;
    }
    public void toast(String message) {

        if(!isOn || isFinishing())
            return;

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public void showDialog(String title, String message) {

        if(!isOn || isFinishing())
            return;

        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(title).setMessage(message)
                .setPositiveButton("OK", null).create().show();
    }
}
