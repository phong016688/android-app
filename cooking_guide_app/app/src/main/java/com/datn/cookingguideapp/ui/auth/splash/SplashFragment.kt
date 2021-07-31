package com.datn.cookingguideapp.ui.auth.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.datn.cookingguideapp.R
import com.datn.cookingguideapp.databinding.FragmentSplashBinding
import com.datn.cookingguideapp.utils.NetworkUtils
import com.datn.cookingguideapp.utils.viewBindings
import io.reactivex.rxjava3.disposables.Disposable
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SplashFragment : Fragment(R.layout.fragment_splash) {
    private val viewBinding by viewBindings(FragmentSplashBinding::bind)
    private val viewModel by viewModel<SplashViewModel>()
    private var disposable: Disposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposable = NetworkUtils.ConnectionLiveData(requireContext()).subject
            .subscribe { viewModel.checkAuth(it) }
    }

    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }
}

private inline fun <T : Any, R> Optional<T>.fold(
    crossinline none: () -> R,
    crossinline some: (T) -> R,
): R =
    if (isPresent) some(get())
    else none()
