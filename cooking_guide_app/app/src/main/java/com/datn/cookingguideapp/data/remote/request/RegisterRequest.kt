package com.datn.cookingguideapp.data.remote.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("full_name")
    val fullName: String,
    val avatar: String,
    val email: String,
    val password: String,
    val birthday: String,
    val address: String,
    val gender: String = "MALE",
)
