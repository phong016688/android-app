package com.datn.cookingguideapp.data.cache

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.ConcurrentHashMap

interface BaseAppSharedPreferences {
    fun <T> get(key: String, clazz: Class<T>): T
    fun <T> put(key: String, data: T)
    fun clear()
    fun clearKey(key: String)
    fun <T> observe(key: String, clazz: Class<T>): Observable<T>
}

@Suppress("UNCHECKED_CAST")
class AppPreferences(context: Context) : BaseAppSharedPreferences {
    private val sharedPreferences by lazy {
        context.getSharedPreferences(AppPrefKey.APP_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    private val mListeners = ConcurrentHashMap<OnSharedPreferenceChangeListener, Unit>()

    override fun <T> get(key: String, clazz: Class<T>): T {
        return mapperData(key, clazz)
    }

    override fun <T> put(key: String, data: T) {
        sharedPreferences.edit {
            when (data) {
                is String -> putString(key, data)
                is Boolean -> putBoolean(key, data)
                is Float -> putFloat(key, data)
                is Int -> putInt(key, data)
                is Long -> putLong(key, data)
                else -> putString(key, Gson().toJson(data))
            }
            apply()
        }
    }

    override fun clear() {
        sharedPreferences.edit {
            clear()
            apply()
        }
    }

    override fun clearKey(key: String) {
        sharedPreferences.edit {
            remove(key)
            apply()
        }
    }

    override fun <T> observe(key: String, clazz: Class<T>): Observable<T> {
        return Observable
            .create<Unit> { emitter ->
                val listener = OnSharedPreferenceChangeListener { _, changedKey ->
                    if (changedKey == key) {
                        emitter.onNext(Unit)
                    }
                }
                mListeners[listener] = Unit
                sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
                emitter.setCancellable {
                    sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
                    mListeners.remove(listener)
                }
            }
            .startWithItem(Unit)
            .map { mapperData(key, clazz) }
    }

    private fun <T> mapperData(key: String, clazz: Class<T>): T {
        return when (clazz) {
            String::class.java -> sharedPreferences.getString(key, "").let { it as T }
            Boolean::class.java -> sharedPreferences.getBoolean(key, false).let { it as T }
            Float::class.java -> sharedPreferences.getFloat(key, 0f).let { it as T }
            Int::class.java -> sharedPreferences.getInt(key, 0).let { it as T }
            Long::class.java -> sharedPreferences.getLong(key, 0).let { it as T }
            else -> Gson().fromJson(sharedPreferences.getString(key, ""), clazz)
        }
    }
}