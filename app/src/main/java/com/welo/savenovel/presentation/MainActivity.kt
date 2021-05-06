package com.welo.savenovel.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.obsez.android.lib.filechooser.ChooserDialog
import com.welo.savenovel.R
import com.welo.savenovel.domain.interactor.textparser.TextParser
import com.welo.savenovel.domain.model.Article
import com.welo.savenovel.exception.*
import com.welo.savenovel.presentation.ui.FloatingToolbar
import com.welo.savenovel.presentation.utility.BaseActivity
import com.welo.savenovel.presentation.utility.Define
import com.welo.savenovel.presentation.utility.SharedPrefMgr
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

@SuppressLint("CheckResult")
class MainActivity : BaseActivity(), PermissionCallbacks {

    @BindView(R.id.tv_folder_path)
    lateinit var tvFolderPath: TextView

    @BindView(R.id.tv_url_path)
    lateinit var tvUrlPath: TextView
    private val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val PERMISSION_RESULT_CODE = 100
    private lateinit var floatingToolbar: FloatingToolbar

    var unbinder : Unbinder? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        unbinder = ButterKnife.bind(this)
        initView()
        initFloatingWindow()
    }

    private fun initFloatingWindow() {
        floatingToolbar = FloatingToolbar(applicationContext)
        //floatingToolbar.init();
        floatingToolbar.setOnUrlinputListener { url: String? ->
            TextParser.UrlPaser(url)
                    .flatMapCompletable { article: Article -> saveArticle(article) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ Toast.makeText(context, R.string.save_success, Toast.LENGTH_LONG).show() }) { throwable: Throwable -> errorHandle(throwable) }
        }
    }

    private fun initView() {
        val filePath = SharedPrefMgr.loadSharedPref(context, Define.SPFS_SAVE_DIRECTORY, "", Define.SPFS_CATEGORY)
        tvFolderPath.text = filePath
    }

    fun folderChooser(view: View?) {
        if (EasyPermissions.hasPermissions(this, *perms)) {
            folderChooserDailog()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.file_permission),
                    PERMISSION_RESULT_CODE, *perms)
        }
    }

    private fun folderChooserDailog() {
        ChooserDialog(this)
            .withFilter(true, false)
            .withStartFile(tvFolderPath.text as String?) // to handle the result(s)
            .withChosenListener({ dir, dirFile ->
                SharedPrefMgr.saveSharedPref(context, Define.SPFS_SAVE_DIRECTORY, dir, Define.SPFS_CATEGORY)
                Timber.d("path = $dir")
                tvFolderPath.text = dir
            })
            .build()
            .show()
    }

    fun parseUrl(view: View?) {
        val url = tvUrlPath.text.toString()
        showProgressDialog()
        hideKeybroad()
        TextParser.UrlPaser(url)
                .flatMapCompletable { article: Article -> saveArticle(article) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { hideProgressDialog() }
                .subscribe({
                    showToast(R.string.save_success)
                    tvUrlPath.text = ""
                }) { throwable: Throwable -> errorHandle(throwable) }

    }

    fun lofterParseUrl(view: View?) {
        val url = "Test lofter url"
        TextParser.UrlPaser(url)
                .flatMapCompletable { article: Article -> saveArticle(article) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ showToast(R.string.save_success) }) { throwable: Throwable -> errorHandle(throwable) }
    }

    fun ao3ParseUrlSingle(view: View?) {
        val url = "Test ao3 url"
        TextParser.UrlPaser(url)
                .flatMapCompletable { article: Article -> saveArticle(article) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ showToast(R.string.save_success) }) { throwable: Throwable -> errorHandle(throwable) }
    }

    fun ao3ParseUrlMuti(view: View?) {
        val url = "Test ao3 url"
        TextParser.UrlPaser(url)
                .flatMapCompletable { article: Article -> saveArticle(article) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ showToast(R.string.save_success) }) { throwable: Throwable -> errorHandle(throwable) }
    }

    fun showFloatWindow(view: View?) {
        floatingToolbar.hide()
    }

    fun saveArticle(article: Article): Completable {
        val fileName = article.title + ".txt"
        val txtContent = articleToTxt(article)
        val filePath = SharedPrefMgr.loadSharedPref(context, Define.SPFS_SAVE_DIRECTORY, "", Define.SPFS_CATEGORY)
        return Completable.create { emitter: CompletableEmitter ->
            if (!fileName.isEmpty()) {
                try {
                    val outFile = File(filePath, fileName)
                    val outputStream = FileOutputStream(outFile)
                    outputStream.write(txtContent.toByteArray())
                    emitter.onComplete()
                } catch (e: IOException) {
                    e.printStackTrace()
                    emitter.onError(SaveFileFailedException(e.message))
                }
            } else {
                emitter.onError(NullFolderException())
            }
        }
    }

    fun articleToTxt(article: Article): String {
        val copyright = String.format(Locale.getDefault(), getString(R.string.txt_copyright_warning), article.url)
        return copyright + "\n\n" +
                article.title + "\n\n" +  //getString(R.string.txt_summary) + article.getSummary() + "\n\n" +
                article.content + "\n\n"
    }

    private fun errorHandle(throwable: Throwable) {
        throwable.printStackTrace()
        if (throwable is NoSupportWebException) {
            showToast(R.string.error_no_support_web)
        } else if (throwable is EmptyURLException) {
            showToast(R.string.error_empty_url)
        } else if (throwable is SaveFileFailedException) {
            showToast(R.string.error_save_failed)
        } else if (throwable is NullFolderException) {
            showToast(R.string.error_null_folder)
        } else if (throwable is ParseURLFailedException) {
            showToast(R.string.error_parse_url_failed)
        } else {
            showToast(throwable.message)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_RESULT_CODE) {
            val granted = ArrayList<String>()
            val denied = ArrayList<String>()
            for (i in permissions.indices) {
                val perm = permissions[i]
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    granted.add(perm)
                } else {
                    denied.add(perm)
                }
            }
            if (!denied.isEmpty()) {
                onPermissionsDenied(requestCode, denied)
            } else {
                folderChooserDailog()
            }
        }
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        folderChooserDailog()
    }

    override fun onPermissionsDenied(requestCode: Int, permsList: List<String>) {
        val perms = permsList.toTypedArray()
        if (perms.size > 0) {
            EasyPermissions.requestPermissions(this, getString(R.string.file_permission), PERMISSION_RESULT_CODE, *perms)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder!!.unbind()
    }
}