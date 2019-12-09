package com.welo.savenovel.presentation;

import com.codekidlabs.storagechooser.StorageChooser;
import com.welo.savenovel.R;
import com.welo.savenovel.domain.interactor.textparser.TextParser;
import com.welo.savenovel.domain.model.Article;
import com.welo.savenovel.exception.EmptyURLException;
import com.welo.savenovel.exception.NoSupportWebException;
import com.welo.savenovel.exception.NullFolderException;
import com.welo.savenovel.exception.ParseURLFailedException;
import com.welo.savenovel.exception.SaveFileFailedException;
import com.welo.savenovel.presentation.ui.FloatingToolbar;
import com.welo.savenovel.presentation.utility.BaseActivity;
import com.welo.savenovel.presentation.utility.Define;
import com.welo.savenovel.presentation.utility.SharedPrefMgr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

@SuppressLint("CheckResult")
public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.tv_folder_path)
    TextView tvFolderPath;
    @BindView(R.id.tv_url_path)
    TextView tvUrlPath;


    private String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private final int PERMISSION_RESULT_CODE = 100;
    private FloatingToolbar floatingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initFloatingWindow();
    }

    private void initFloatingWindow() {
        floatingToolbar = new FloatingToolbar(getApplicationContext());
        //floatingToolbar.init();
        floatingToolbar.setOnUrlinputListener(url -> {
            TextParser.UrlPaser(url)
                    .flatMapCompletable(this::saveArticle)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Toast.makeText(context, R.string.save_success, Toast.LENGTH_LONG).show();
                    }, this::errorHandle);
        });
    }

    private void initView() {
        String filePath = SharedPrefMgr.loadSharedPref(context, Define.SPFS_SAVE_DIRECTORY, "", Define.SPFS_CATEGORY);
        tvFolderPath.setText(filePath);
    }

    public void folderChooser(View view) {
        if (EasyPermissions.hasPermissions(this, perms)) {
            folderChooserDailog();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.file_permission),
                    PERMISSION_RESULT_CODE, perms);
        }
    }

    private void folderChooserDailog() {
        final StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(activity)
                .withFragmentManager(getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();

        chooser.setOnSelectListener(path -> {
            // e.g /storage/emulated/0/Documents
            SharedPrefMgr.saveSharedPref(context, Define.SPFS_SAVE_DIRECTORY, path, Define.SPFS_CATEGORY);
            Timber.d("path = " + path);
            tvFolderPath.setText(path);
        });
        chooser.show();
    }

    public void parseUrl(View view) {
        String url = tvUrlPath.getText().toString();
        showProgressDialog();
        hideKeybroad();
        TextParser.UrlPaser(url)
                .flatMapCompletable(this::saveArticle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::hideProgressDialog)
                .subscribe(() -> {
                    showToast(R.string.save_success);
                    tvUrlPath.setText("");
                }, this::errorHandle);

    }

    public void lofterParseUrl(View view) {
        String url = "Test lofter url";
        TextParser.UrlPaser(url)
                .flatMapCompletable(this::saveArticle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    showToast(R.string.save_success);
                }, this::errorHandle);

    }

    public void ao3ParseUrlSingle(View view) {
        String url = "Test ao3 url";
        TextParser.UrlPaser(url)
                .flatMapCompletable(this::saveArticle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    showToast(R.string.save_success);
                }, this::errorHandle);

    }

    public void ao3ParseUrlMuti(View view) {
        String url = "Test ao3 url";
        TextParser.UrlPaser(url)
                .flatMapCompletable(this::saveArticle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    showToast(R.string.save_success);
                }, this::errorHandle);
    }

    public void showFloatWindow(View view) {
        floatingToolbar.hide();
    }


    public Completable saveArticle(Article article) {
        String fileName = article.getTitle() + ".txt";
        String txtContent = articleToTxt(article);
        String filePath = SharedPrefMgr.loadSharedPref(context, Define.SPFS_SAVE_DIRECTORY, "", Define.SPFS_CATEGORY);

        return Completable.create(emitter -> {
            if (!fileName.isEmpty()) {
                try {
                    File outFile = new File(filePath, fileName);
                    FileOutputStream outputStream = new FileOutputStream(outFile);
                    outputStream.write(txtContent.getBytes());
                    emitter.onComplete();
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(new SaveFileFailedException(e.getMessage()));
                }
            } else {
                emitter.onError(new NullFolderException());
            }
        });
    }

    public String articleToTxt(Article article) {
        String copyright = String.format(Locale.getDefault(), getString(R.string.txt_copyright_warning), article.getUrl());
        String txtContent = copyright + "\n\n" +
                article.getTitle() + "\n\n" +
                //getString(R.string.txt_summary) + article.getSummary() + "\n\n" +
                article.getContent() + "\n\n";
        return txtContent;
    }


    private void errorHandle(Throwable throwable) {
        throwable.printStackTrace();
        if (throwable instanceof NoSupportWebException) {
            showToast(R.string.error_no_support_web);
        } else if (throwable instanceof EmptyURLException) {
            showToast(R.string.error_empty_url);
        } else if (throwable instanceof SaveFileFailedException) {
            showToast(R.string.error_save_failed);
        } else if (throwable instanceof NullFolderException) {
            showToast(R.string.error_null_folder);
        } else if (throwable instanceof ParseURLFailedException) {
            showToast(R.string.error_parse_url_failed);
        } else {
            showToast(throwable.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_RESULT_CODE) {
            ArrayList<String> granted = new ArrayList<>();
            ArrayList<String> denied = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                String perm = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    granted.add(perm);
                } else {
                    denied.add(perm);
                }
            }
            if (!denied.isEmpty()) {
                onPermissionsDenied(requestCode, denied);
            } else {
                folderChooserDailog();
            }
        }
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        folderChooserDailog();

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> permsList) {
        String[] perms = new String[permsList.size()];
        perms = permsList.toArray(perms);
        if (perms.length > 0) {
            EasyPermissions.requestPermissions(this, getString(R.string.file_permission), PERMISSION_RESULT_CODE, perms);
        }
    }
}
