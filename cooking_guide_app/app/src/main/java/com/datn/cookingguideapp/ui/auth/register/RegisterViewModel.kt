package com.datn.cookingguideapp.ui.auth.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.datn.cookingguideapp.domain.model.User
import com.datn.cookingguideapp.domain.repository.AuthRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers

class RegisterViewModel(private val authRepo: AuthRepository) : ViewModel() {
    private val mutableLV = MutableLiveData<User>()
    val liveData: LiveData<User> = mutableLV
    private val mutableLVErr = MutableLiveData<Throwable>()
    val liveDataErr: LiveData<Throwable> = mutableLVErr
    private val compositeDisposable = CompositeDisposable()

    fun register(
        fullName: String,
        avatar: String,
        email: String,
        password: String,
        birthday: String,
        address: String,
    ) {
        authRepo.register(fullName, avatar, email, password, birthday, address)
            .filter { validateField(fullName, avatar, email, password, birthday, address) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapSingle { authRepo.login(email, password) }
            .subscribe(
                { mutableLV.postValue(it) },
                { mutableLVErr.postValue(it) }
            )
            .addTo(compositeDisposable)
    }

    private fun validateField(
        fullName: String,
        avatar: String,
        email: String,
        password: String,
        birthday: String,
        address: String,
    ): Boolean {
        val error = when {
            email.isEmpty() -> Throwable("Email không thể trống")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Throwable("Email sai định dạng")
            password.isEmpty() -> Throwable("Mật khẩu không thể trống")
            fullName.isEmpty() -> Throwable("Tên không thể trống")
            avatar.isEmpty() -> Throwable("Ảnh đại diện không thể trống")
            birthday.isEmpty() -> Throwable("Ngày sinh không thể trống")
            address.isEmpty() -> Throwable("Địa chỉ không thể trống")
            else -> null
        }
        mutableLVErr.postValue(error ?: return true)
        return false
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
