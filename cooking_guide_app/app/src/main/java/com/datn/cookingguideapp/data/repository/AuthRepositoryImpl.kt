package com.datn.cookingguideapp.data.repository

import com.datn.cookingguideapp.data.cache.AppPrefKey
import com.datn.cookingguideapp.data.cache.AppPreferences
import com.datn.cookingguideapp.data.local.AppRoomDatabase
import com.datn.cookingguideapp.data.remote.RestApi
import com.datn.cookingguideapp.data.remote.request.LoginRequest
import com.datn.cookingguideapp.data.remote.request.RegisterRequest
import com.datn.cookingguideapp.domain.model.User
import com.datn.cookingguideapp.domain.repository.AuthRepository
import com.datn.cookingguideapp.utils.toObservable
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class AuthRepositoryImpl(
    private val serviceApi: RestApi,
    private val preferences: AppPreferences,
    private val roomDB: AppRoomDatabase,
) : AuthRepository {

    override fun login(userName: String, password: String): Single<User> {
        return serviceApi.login(LoginRequest(userName, password))
            .flatMap { loginResponse ->
                preferences.put(AppPrefKey.Auth.ACCESS_TOKEN, loginResponse.accessToken)
                preferences.put(AppPrefKey.Auth.EXPIRES_IN, loginResponse.expiresIn)
                roomDB.userDao().insertUser(loginResponse.user)
                Single.just(loginResponse.user)
            }.subscribeOn(Schedulers.io())
    }

    override fun logout(): Single<Unit> {
        return Completable.fromCallable { roomDB.userDao().deleteAll() }
            .doOnComplete {
                preferences.clearKey(AppPrefKey.Auth.ACCESS_TOKEN)
                preferences.clearKey(AppPrefKey.Auth.EXPIRES_IN)
            }.toSingle { }
    }

    override fun getCurrentUser(): Observable<User> {
        return roomDB.userDao().getUsers().toObservable().map { it.first() }
    }


    override fun checkAuth(isConnect: Boolean): Observable<Optional<User>> {
        val userRemoteOb = serviceApi.getProfile().flatMap {
            roomDB.userDao().insertUser(it)
            Observable.just(Optional.of(it))
        }
        return Observable.just(isConnect)
            .flatMap { connected ->
                if (connected) userRemoteOb
                else getCurrentUser().map { Optional.of(it) }
            }
            .map { optional ->
                optional.filter {
                    preferences.get(AppPrefKey.Auth.ACCESS_TOKEN, String::class.java).isNotEmpty()
                }
            }
            .onErrorReturnItem(Optional.empty())
            .observeOn(Schedulers.io())
            .doOnNext {
                if (!it.isPresent) {
                    roomDB.userDao().deleteAll()
                    preferences.clearKey(AppPrefKey.Auth.ACCESS_TOKEN)
                    preferences.clearKey(AppPrefKey.Auth.EXPIRES_IN)
                }
            }
    }

    override fun register(
        fullName: String,
        avatar: String,
        email: String,
        password: String,
        birthday: String,
        address: String,
    ): Observable<User> {
        val request = RegisterRequest(fullName, avatar, email, password, birthday, address)
        return serviceApi.register(request)
    }
}
