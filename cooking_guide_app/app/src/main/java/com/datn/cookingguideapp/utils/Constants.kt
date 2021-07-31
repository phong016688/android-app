package com.datn.cookingguideapp.utils

object Constants {
    const val DOC_ID_CHAT="DOC_ID_CHAT"
    const val INTENT_EXTRA_DISH = "INTENT_EXTRA_DISH"
    const val INTENT_EXTRA_TYPE = "INTENT_EXTRA_TYPE"
    const val ID_STREAM_EXTRA = "ID_STREAM"
    const val RECEIVE_USER = "RECEIVE_USER"


    fun String.fromTypeMeal() = when (this) {
        "BREAKFAST" -> "Sáng"
        "LUNCH" -> "Trưa"
        "DINNER" -> "Tối"
        else -> ""
    }

    fun String.fromLevel() = when (this) {
        "EASY" -> "Dễ"
        "MEDI" -> "Vừa"
        "HIGH" -> "Khó"
        else -> ""
    }
}