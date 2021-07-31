package com.datn.cookingguideapp.domain.model

data class Error(
    val statusCode: Int,
    val message: String,
    val error: String,
)