package com.lucascabrales.montecarlosimulation.helpers;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.lucascabrales.montecarlosimulation.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by lucascabrales on 11/8/17.
 */

public class LoadingDialogHelper {

    private Context mActivity;
    private AlertDialog mDialog;
    private AVLoadingIndicatorView mAvi;

    public LoadingDialogHelper(Context context) {
        mActivity = context;

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.Theme_TransparentDialog);
        builder.setView(R.layout.dialog_loading);

        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);
//        mDialog.setCancelable(false);
    }

    public void show() {
        mDialog.show();
        if (mAvi == null) {
            mAvi = (AVLoadingIndicatorView) mDialog.findViewById(R.id.avi);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            };

//            mDialog.findViewById(R.id.btn_cancel).setOnClickListener(listener);
        }
//        mAvi.show();
    }

    public void dismiss() {
        mDialog.dismiss();
        /*if (mAvi != null)
            mAvi.hide();*/
    }
}
