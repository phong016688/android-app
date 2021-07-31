package com.datn.cookingguideapp.data.remote

import com.datn.cookingguideapp.data.remote.request.LoginRequest
import com.datn.cookingguideapp.data.remote.request.RegisterRequest
import com.datn.cookingguideapp.data.remote.response.LoginResponse
import com.datn.cookingguideapp.domain.model.User
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RestApi {
    //------- Method POST --------
    @POST("/auth/register")
    fun register(@Body data: RegisterRequest): Observable<User>

    @POST("/auth/login")
    fun login(@Body loginRequest: LoginRequest): Single<LoginResponse>

    //----------Method GET--------
    @GET("/users/me")
    fun getProfile(): Observable<User>
}
