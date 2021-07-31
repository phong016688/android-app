package com.datn.cookingguideapp.ui.auth.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.fragment.findNavController
import com.datn.cookingguideapp.R
import com.datn.cookingguideapp.databinding.FragmentLoginBinding
import com.datn.cookingguideapp.ui.auth.login.LoginContract.Action
import com.datn.cookingguideapp.ui.auth.register.RegisterFragment
import com.datn.cookingguideapp.ui.home.HomeActivity
import com.datn.cookingguideapp.utils.viewBindings
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val viewBinding by viewBindings(FragmentLoginBinding::bind)
    private val viewModel by viewModel<LoginViewModel>()
    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() = viewBinding.apply {
        val emailChangeOb = emailEditText.textChanges()
            .debounce(500L, TimeUnit.MILLISECONDS)
            .map { email -> Action.ValidateEmailAction(email.toString()) }
        val passwordChangeOb = passwordEditText.textChanges()
            .debounce(500L, TimeUnit.MILLISECONDS)
            .map { password -> Action.ValidatePasswordAction(password.toString()) }
            .map { _ -> Action.LoginAction("${emailEditText.text}", "${passwordEditText.text}") }
        val forgotPassClickOb = forgetPasswordButton.clicks()
            .map { _ -> Action.ForgotPasswordAction }

        Observable.merge(
            listOf(
                Observable.just(Action.InitialAction),
                emailChangeOb,
                passwordChangeOb, forgotPassClickOb,

                )
        ).subscribeOn(AndroidSchedulers.mainThread())
            .subscribe { viewModel.processAction(it) }
            .addTo(compositeDisposable)


        viewModel.observeState()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe {
                emailEditText.error = it.validateEmailMessage
                passwordEditText.error = it.validatePassword
                if (it.error.isNotEmpty()) {
                    Log.d("#### Login", it.error)
                    Toast.makeText(context,
                        "Thông tin đăng nhập không chính xác",
                        Toast.LENGTH_SHORT).show()
                }
                when (it.moveTo) {
                    HomeActivity::class -> {
                        startActivity(HomeActivity.makeActivity(requireContext()))
                        activity?.finish()
                    }
                    RegisterFragment::class -> {
                        findNavController().navigate(ActionOnlyNavDirections(R.id.action_login_to_register))
                    }
                    else -> return@subscribe
                }
            }
            ?.addTo(compositeDisposable)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
