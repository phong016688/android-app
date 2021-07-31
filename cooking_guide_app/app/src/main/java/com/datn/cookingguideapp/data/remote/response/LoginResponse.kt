package com.datn.cookingguideapp.data.remote.response

import com.datn.cookingguideapp.domain.model.User
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken : String,
    val user: User,
    @SerializedName("expires_in")
    val expiresIn: String
)