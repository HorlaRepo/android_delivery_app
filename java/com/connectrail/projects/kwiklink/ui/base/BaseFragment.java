package com.connectrail.projects.kwiklink.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.connectrail.projects.kwiklink.R;

import butterknife.ButterKnife;

/**
 * Created by root on 9/24/17.
 */

public class BaseFragment extends Fragment {

    private volatile boolean isOn = false;
    @Override
    public void onPause() {
        super.onPause();
        isOn = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
        isOn = true;
    }
    public void toast(String message) {

        if(!isOn || isDetached())
            return;

        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
    public void showDialog(String title, String message) {

        if(!isOn || isDetached())
            return;

        new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle)
                .setTitle(title).setMessage(message)
                .setPositiveButton("OK", null).create().show();
    }
}
