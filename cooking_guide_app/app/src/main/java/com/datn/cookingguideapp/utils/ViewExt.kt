package com.datn.cookingguideapp.utils

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String?) {
    Toast.makeText(this, message ?: return, Toast.LENGTH_SHORT).show()
}