package com.datn.cookingguideapp

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.datn.cookingguideapp.data.cache.AppPrefKey
import com.datn.cookingguideapp.data.cache.AppPreferences
import com.datn.cookingguideapp.ui.auth.AuthActivity
import com.datn.cookingguideapp.utils.NetworkUtils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import org.koin.android.ext.android.inject

abstract class BaseActivity(@LayoutRes private val idRes: Int) : AppCompatActivity(idRes) {
    private val compositeDisposable = CompositeDisposable()
    private val preferences by inject<AppPreferences>()
    private var dialogNetwork: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialog()
        registerNetWorkCallback()
    }

    private fun initDialog() {
        dialogNetwork = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Bạn đang mất kết nối")
            .setMessage("Hãy kiểm tra kết nối và thử lại?")
            .create()
    }

    private fun registerNetWorkCallback() {
        NetworkUtils.ConnectionLiveData(this).subject
            .subscribe {
                if (it) {
                    if (dialogNetwork?.isShowing == false) return@subscribe
                    dialogNetwork?.hide()
                } else {
                    dialogNetwork?.show()
                }
            }.addTo(compositeDisposable)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}