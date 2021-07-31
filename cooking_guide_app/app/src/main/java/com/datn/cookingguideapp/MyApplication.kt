package com.datn.cookingguideapp

import android.app.Application
import android.util.Log
import com.datn.cookingguideapp.di.repositoryModule
import com.datn.cookingguideapp.di.serviceModule
import com.datn.cookingguideapp.di.viewModelModule
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler { Log.d("##### rxError: ", it.message.toString()) }
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApplication)
            modules(viewModelModule, repositoryModule, serviceModule)
        }
    }
}
