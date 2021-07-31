package com.datn.cookingguideapp.ui.auth.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.datn.cookingguideapp.domain.model.User
import com.datn.cookingguideapp.domain.repository.AuthRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class SplashViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val liveDataAuth = MutableLiveData<Optional<User>>()

    fun getLiveDataAuth(): LiveData<Optional<User>> = liveDataAuth

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun checkAuth(isConnect: Boolean) {
        authRepository.checkAuth(isConnect)
            .delay(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { liveDataAuth.postValue(it) }
            .addTo(compositeDisposable)
    }
}