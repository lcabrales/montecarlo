package com.lucascabrales.montecarlosimulation.helpers;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.lucascabrales.montecarlosimulation.R;

/**
 * Created by lucascabrales on 11/7/17.
 */

public class AlertDialogHelper {

    private AppCompatActivity mActivity;
    private AlertDialog mDialog;

    public AlertDialogHelper(AppCompatActivity ctx) {
        mActivity = ctx;
    }

    public void show(String title, String msg) {
        if (mDialog != null && mDialog.isShowing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.Theme_AlertDialog);
        builder.setView(R.layout.dialog_message);
        builder.setTitle(title);

//        builder.setMessage(msg);

        builder.setPositiveButton(mActivity.getString(R.string.accept), null);
        mDialog = builder.create();
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);

        ((TextView) mDialog.findViewById(R.id.tv_msg)).setText(msg);
    }

    public void showWithFinish(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.Theme_AlertDialog);
        builder.setView(R.layout.dialog_message);
        builder.setTitle(title);
        builder.setCancelable(false);

//        builder.setMessage(msg);

        DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.finish();
            }
        };

        builder.setPositiveButton(mActivity.getString(R.string.accept), cancel);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        ((TextView) dialog.findViewById(R.id.tv_msg)).setText(msg);
    }

    public void showWithIntent(String title, String msg, Intent intent) {
        final Intent intentE = intent;
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.Theme_AlertDialog);
        builder.setView(R.layout.dialog_message);
        builder.setTitle(title);
        builder.setCancelable(false);

//        builder.setMessage(msg);

        DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.startActivity(intentE);
            }
        };

        builder.setPositiveButton(mActivity.getString(R.string.accept), cancel);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        ((TextView) dialog.findViewById(R.id.tv_msg)).setText(msg);
    }
}

