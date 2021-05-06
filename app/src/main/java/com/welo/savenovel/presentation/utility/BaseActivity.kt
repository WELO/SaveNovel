package com.welo.savenovel.presentation.utility

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.welo.savenovel.R
import timber.log.Timber

/**
 * Created by Amy on 2019/4/1
 */
abstract class BaseActivity : AppCompatActivity() {
    protected var context: Context? = null
    protected var activity: Activity? = null
    protected var toolbar: Toolbar? = null
    private var progressDialog: AlertDialog? = null
    private var progressDialogMsg: TextView? = null
    protected var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        activity = this
        initProgressDialog()
    }

    private fun initProgressDialog() {
        progressDialog = AlertDialog.Builder(context, R.style.CustomProgressDialog).create()
        val loadView = LayoutInflater.from(context).inflate(R.layout.layout_progress_dialog, null)
        progressDialog?.setView(loadView, 0, 0, 0, 0)
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.setCancelable(false)
        progressDialogMsg = loadView.findViewById(R.id.tvTip)
        progressDialogMsg?.setText(R.string.common_loading)
    }

    protected fun showProgressDialog() {
        if (!progressDialog!!.isShowing) {
            Timber.d("progressDialog.show()")
            progressDialog!!.show()
        }
    }

    protected fun showProgressDialog(msg: String?) {
        progressDialogMsg!!.text = msg
        if (!progressDialog!!.isShowing) {
            Timber.d("progressDialog.show()")
            progressDialog!!.show()
        }
    }

    protected fun hideProgressDialog() {
        if (null != progressDialog && progressDialog!!.isShowing) {
            Timber.d("progressDialog.dismiss()")
            progressDialog!!.dismiss()
        }
    }

    public override fun onStop() {
        super.onStop()
    }

    protected fun getDialog(titleRes: Int, contentRes: Int): AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        return builder.setTitle(titleRes).setMessage(contentRes).setCancelable(false)
    }

    protected fun getDialog(title: String?, content: String?): AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        return builder.setTitle(title).setMessage(content).setCancelable(false)
    }

    protected fun showToast(msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    protected fun showToast(msgRes: Int) {
        Toast.makeText(context, msgRes, Toast.LENGTH_LONG).show()
    }

    protected fun hideKeybroad() {
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun onPageCancel(view: View?) {
        onBackPressed()
    }
}