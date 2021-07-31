package com.datn.cookingguideapp.ui.auth.login

import android.util.Patterns
import kotlin.reflect.KClass

interface LoginContract {
    data class LoginState(
        val isLoading: Boolean = false,
        val error: String = "",
        val validateEmailMessage: String? = null,
        val validatePassword: String? = null,
        val moveTo: KClass<out Any> = Any::class
    )

    data class LoginResult(
        val action: Action,
        val state: LoginState
    )

    sealed class Action {

        abstract fun filter(): Boolean

        data class LoginAction(
            val email: String,
            val password: String
        ) : Action() {
            override fun filter(): Boolean = email.isNotEmpty() && password.isNotEmpty()
        }

        data class ValidateEmailAction(
            val email: String
        ) : Action() {
            override fun filter(): Boolean = email.isNotEmpty()

            fun validateEmail(): String? {
                return if (email.matches(Patterns.EMAIL_ADDRESS.toRegex()) && email.isNotEmpty()) null else "Email not validate"
            }
        }

        data class ValidatePasswordAction(
            val password: String
        ) : Action() {
            override fun filter(): Boolean = password.isNotEmpty()

            fun validatePassword(): String? {
                return if (password.length in 6..20) null else "Password not validate"
            }
        }

        object SignAction : Action() {
            override fun filter(): Boolean = true
        }

        object ForgotPasswordAction : Action() {
            override fun filter(): Boolean = true
        }

        object InitialAction : Action() {
            override fun filter(): Boolean = true
        }
    }
}