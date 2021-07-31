package com.datn.cookingguideapp.domain.repository

import com.datn.cookingguideapp.domain.model.User
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*


interface AuthRepository {
    fun login(userName: String, password: String): Single<User>
    fun logout(): Single<Unit>
    fun getCurrentUser(): Observable<User>
    fun checkAuth(isConnect: Boolean): Observable<Optional<User>>

    //Users
    fun register(
        fullName: String,
        avatar: String,
        email: String,
        password: String,
        birthday: String,
        address: String,
    ): Observable<User>
}