package com.datn.cookingguideapp.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject


object NetworkUtils {
    class ConnectionLiveData(private val context: Context) {
        val subject: BehaviorSubject<Boolean>
        private val networkCallback: ConnectivityManager.NetworkCallback
        private val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        private val connManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        init {
            subject = BehaviorSubject.createDefault(isWiFiConnected(context))
            networkCallback = getConnectivityCallback()
            connManager.registerNetworkCallback(networkRequest, networkCallback)
            subject
                .distinctUntilChanged()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { unregisterNetworkCallback() }
        }

        private fun unregisterNetworkCallback() {
            connManager.unregisterNetworkCallback(networkCallback)
        }

        private fun getConnectivityCallback(): ConnectivityManager.NetworkCallback {
            return object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities,
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    subject.onNext(isWiFiConnected(context))
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    subject.onNext(isWiFiConnected(context))
                }
            }
        }

        private fun isWiFiConnected(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        }
    }
}