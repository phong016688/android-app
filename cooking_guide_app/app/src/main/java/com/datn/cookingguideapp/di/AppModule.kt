package com.datn.cookingguideapp.di

import android.content.Context
import com.datn.cookingguideapp.BuildConfig
import com.datn.cookingguideapp.data.cache.AppPreferences
import com.datn.cookingguideapp.data.local.AppRoomDatabase
import com.datn.cookingguideapp.data.remote.RestApi
import com.datn.cookingguideapp.data.remote.interceptor.InterceptorImpl
import com.datn.cookingguideapp.data.repository.AuthRepositoryImpl
import com.datn.cookingguideapp.domain.repository.AuthRepository
import com.datn.cookingguideapp.ui.auth.login.LoginViewModel
import com.datn.cookingguideapp.ui.auth.register.RegisterViewModel
import com.datn.cookingguideapp.ui.auth.splash.SplashViewModel
import com.google.gson.Gson
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
}

val serviceModule = module {
    single<Interceptor> { InterceptorImpl(get()) }
    single { AppPreferences(androidContext()) }
    single { buildOKHttpClient(androidContext(), get()) }
    single { buildGSON() }
    single { createRetrofit(get(), get()) }
    single { AppRoomDatabase.getDatabase(androidContext()) }
}

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
}

/**
 * Build RestAPI using retrofit
 * @param gson: Gson,
 * @param okHttpClient: OkHttpClient
 */
private fun createRetrofit(gson: Gson, okHttpClient: OkHttpClient): RestApi {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(okHttpClient)
        .build()
        .create(RestApi::class.java)
}

/**
 * Build OKHttpClient
 * @param context: Context
 * @param  interceptor: Interceptor
 */
private fun buildOKHttpClient(context: Context, interceptor: Interceptor): OkHttpClient {
    return OkHttpClient.Builder().apply {
        cache(Cache(context.applicationContext.cacheDir, 10 * 1024 * 1024))
        addInterceptor(interceptor)
        readTimeout(15L, TimeUnit.SECONDS)
        connectTimeout(15L, TimeUnit.SECONDS)
        callTimeout(15L, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(logging)
        }
    }.build()
}

private fun buildGSON(): Gson = Gson()