package com.datn.cookingguideapp.domain.model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.intellij.lang.annotations.Identifier
import java.util.*

@Entity(tableName = "user")
@SuppressLint("ParcelCreator")
@Parcelize
data class User(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    @SerializedName("_id")
    override val id: String,
    @ColumnInfo(name = "full_name")
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("email")
    val email: String,
    val birthday: String?,
    val address: String?,
    @ColumnInfo(name = "plan_id")
    @SerializedName("plan_id")
    val planId: String?,
    @ColumnInfo(name = "create_at")
    @SerializedName("createdAt")
    val createAt: String?,
): Parcelable, Identifiable<String>
