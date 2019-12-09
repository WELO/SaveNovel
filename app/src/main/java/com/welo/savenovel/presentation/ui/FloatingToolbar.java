package com.welo.savenovel.presentation.ui;

import com.welo.savenovel.R;
import com.welo.savenovel.databinding.LayoutFloatingToolbarBinding;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;
import timber.log.Timber;

/**
 * Created by Amy on 2019-10-14
 */

public class FloatingToolbar {

    private OnUrlinputListener mOnUrlinputListener;

    public interface OnUrlinputListener {
        public void onUrlInput(String url);
    }

    public void setOnUrlinputListener(OnUrlinputListener onUrlinputListener) {
        mOnUrlinputListener = onUrlinputListener;
    }

    private final Context context;
    private boolean isOpen = false;

    public FloatingToolbar(Context context) {
        this.context = context;
    }

    public void init() {
        LayoutFloatingToolbarBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_floating_toolbar, null, false);
        binding.setIsToolOpen(true);
        binding.btnToolList.setOnClickListener(v -> binding.setIsToolOpen(!binding.getIsToolOpen()));
        binding.btnCloseFloatwindow.setOnClickListener(v -> FloatWindow.get().hide());
        binding.btnSaveNoval.setOnClickListener(v -> {
            showInputUrlDialog();
        });
        FloatWindow
                .with(context)
                .setView(binding.getRoot())
                .setX(Screen.width, 0.3f)
                .setY(Screen.height, 0.8f)
                .setMoveType(MoveType.slide)
                .setMoveStyle(500, new BounceInterpolator())
                .setDesktopShow(true)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .build();
    }

    private void showInputUrlDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_url_input, null,false);
        EditText url = view.findViewById(R.id.et_input_url);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton(R.string.common_confirm, (dialog, which) -> {
                    if (mOnUrlinputListener!=null){
                        mOnUrlinputListener.onUrlInput(url.getText().toString());
                    }
                }).setNegativeButton(R.string.common_cencel, (dialog, which) -> {
                    dialog.dismiss();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void hide() {
        FloatWindow.get().show();
    }

    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSuccess() {
            Timber.d("onSuccess");
        }

        @Override
        public void onFail() {
            Timber.d("onFail");
        }
    };

    private ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {
            Timber.d("onPositionUpdate: x=" + x + " y=" + y);
        }

        @Override
        public void onShow() {
            Timber.d("onShow");
        }

        @Override
        public void onHide() {
            Timber.d("onHide");
        }

        @Override
        public void onDismiss() {
            Timber.d("onDismiss");
        }

        @Override
        public void onMoveAnimStart() {
            Timber.d("onMoveAnimStart");
        }

        @Override
        public void onMoveAnimEnd() {
            Timber.d("onMoveAnimEnd");
        }

        @Override
        public void onBackToDesktop() {
            Timber.d("onBackToDesktop");
        }
    };
}
