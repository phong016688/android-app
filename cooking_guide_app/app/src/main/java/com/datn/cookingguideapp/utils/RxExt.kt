package com.datn.cookingguideapp.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.CheckResult
import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.exceptions.Exceptions
import io.reactivex.rxjava3.internal.disposables.DisposableHelper
import io.reactivex.rxjava3.internal.util.AtomicThrowable
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference


fun <T> LiveData<T>.toObservable(): Observable<T> {
    val handler = Handler(Looper.getMainLooper())

    return object : Observable<T>() {
        override fun subscribeActual(observer: Observer<in T>) {
            val observeDisposable = ObserveDisposable(observer)
            observer.onSubscribe(observeDisposable)
            handler.post { this@toObservable.observeForever(observeDisposable) }
        }

        inner class ObserveDisposable(val observer: Observer<in T>) : Disposable,
            androidx.lifecycle.Observer<T> {

            private val unsubscribed = AtomicBoolean()

            override fun onChanged(t: T?) {
                if (!isDisposed) {
                    if (t == null) {
                        observer.onError(Throwable("value not null"))
                    } else {
                        observer.onNext(t)
                    }
                }
            }

            override fun isDisposed(): Boolean = unsubscribed.get()

            override fun dispose() {
                if (unsubscribed.compareAndSet(false, true)) {
                    handler.post { this@toObservable.removeObserver(this) }
                }
            }
        }
    }
}

@CheckResult
fun <T> Observable<T>.logD(): Observable<T> =
    doOnNext { Log.d("#####-next", it.toString()) }
        .doOnError { Log.d("#####-err", it.toString()) }

@CheckResult
fun <T : Any, R : Any> Observable<T>.exhaustMap(transform: (T) -> Observable<R>): Observable<R> =
    ExhaustMapObservable(this, transform)

class ExhaustMapObservable<T : Any, R : Any>(
    private val source: Observable<T>,
    private val transform: (T) -> Observable<out R>
) : Observable<R>() {
    override fun subscribeActual(observer: Observer<in R>) =
        source.subscribe(ExhaustMapObserver(observer, transform))

    private class ExhaustMapObserver<T : Any, R : Any>(
        private val downstream: Observer<in R>,
        private val transform: (T) -> Observable<out R>
    ) : Observer<T>, Disposable {
        private var upstream: Disposable? = null

        @Volatile
        private var isActive = false
        private val innerObserver = ExhaustMapInnerObserver()
        private val errors = AtomicThrowable()
        private val done = AtomicInteger(1)

        override fun onSubscribe(d: Disposable) {
            if (DisposableHelper.validate(upstream, d)) {
                upstream = d
                downstream.onSubscribe(this)
            }
        }

        override fun onNext(t: T) {
            if (isActive) {
                return
            }

            val o = try {
                transform(t)
            } catch (t: Throwable) {
                Exceptions.throwIfFatal(t)
                upstream!!.dispose()
                onError(t)
                return
            }

            isActive = true
            done.incrementAndGet()
            o.subscribe(innerObserver)
        }

        override fun onError(e: Throwable) {
            if (errors.tryAddThrowableOrReport(e)) {
                onComplete()
            }
        }

        override fun onComplete() {
            if (done.decrementAndGet() == 0) {
                errors.tryTerminateConsumer(downstream)
            }
        }

        override fun dispose() {
            upstream!!.dispose()
            DisposableHelper.dispose(innerObserver)
            errors.tryTerminateAndReport()
        }

        override fun isDisposed() = upstream!!.isDisposed

        private fun innerNext(t: R) {
            downstream.onNext(t)
        }

        private fun innerError(e: Throwable) {
            if (errors.tryAddThrowableOrReport(e)) {
                innerComplete()
            }
        }

        private fun innerComplete() {
            isActive = false
            if (done.decrementAndGet() == 0) {
                errors.tryTerminateConsumer(downstream)
            }
        }

        private inner class ExhaustMapInnerObserver : Observer<R>, AtomicReference<Disposable>() {
            override fun onSubscribe(d: Disposable) = DisposableHelper.replace(this, d).unit
            override fun onNext(t: R) = innerNext(t)
            override fun onError(e: Throwable) = innerError(e)
            override fun onComplete() = innerComplete()
        }
    }
}

@Suppress("unused")
inline val Any?.unit
    get() = Unit

private val RESTART_INDICATOR = Exception()

fun <T> throttleFirstSample(
    time: Long,
    unit: TimeUnit,
    scheduler: Scheduler?
): ObservableTransformer<T, T> {
    return ObservableTransformer { f ->
        f.publish { g ->
            g
                .take(1)
                .concatWith(
                    g
                        .buffer(time, unit, scheduler)
                        .map { v ->
                            if (v.isEmpty()) {
                                throw RESTART_INDICATOR
                            }
                            v[v.size - 1]
                        }
                )
                .retry { e -> e === RESTART_INDICATOR }
        }
    }
}