package com.datn.cookingguideapp.ui.auth.login

import androidx.lifecycle.ViewModel
import com.datn.cookingguideapp.domain.repository.AuthRepository
import com.datn.cookingguideapp.ui.auth.login.LoginContract.Action
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    private val subject = BehaviorSubject.create<Action>()
    private var observableSate: Observable<LoginContract.LoginState>? = null


    fun observeState() = observableSate

    fun processAction(action: Action) {
        subject.onNext(action)
    }
}