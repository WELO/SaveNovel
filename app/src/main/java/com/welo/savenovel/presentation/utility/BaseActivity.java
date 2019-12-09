package com.welo.savenovel.presentation.utility;


import com.welo.savenovel.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;


/**
 * Created by Amy on 2019/4/1
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Context context;
    protected Activity activity;
    protected Toolbar toolbar = null;
    private AlertDialog progressDialog;
    private TextView progressDialogMsg;

    protected Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        this.activity = this;
        initProgressDialog();
    }
    private void initProgressDialog() {
        progressDialog = new AlertDialog.Builder(context, R.style.CustomProgressDialog).create();
        View loadView = LayoutInflater.from(context).inflate(R.layout.layout_progress_dialog, null);
        progressDialog.setView(loadView, 0, 0, 0, 0);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialogMsg = loadView.findViewById(R.id.tvTip);
        progressDialogMsg.setText(R.string.common_loading);
    }

    protected void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            Timber.d("progressDialog.show()");
            progressDialog.show();
        }
    }

    protected void showProgressDialog(String msg){
        progressDialogMsg.setText(msg);
        if (!progressDialog.isShowing()) {
            Timber.d("progressDialog.show()");
            progressDialog.show();
        }
    }

    protected void hideProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            Timber.d("progressDialog.dismiss()");
            progressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    protected AlertDialog.Builder getDialog(int titleRes, int contentRes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder.setTitle(titleRes).setMessage(contentRes).setCancelable(false);
    }

    protected AlertDialog.Builder getDialog(String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder.setTitle(title).setMessage(content).setCancelable(false);
    }

    protected void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    protected void showToast(int msgRes) {
        Toast.makeText(context, msgRes, Toast.LENGTH_LONG).show();
    }

    protected void hideKeybroad() {
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void onPageCancel(View view) {
        onBackPressed();
    }
}
