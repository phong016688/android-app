package com.datn.cookingguideapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.datn.cookingguideapp.domain.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("SELECT * FROM user ORDER BY _id ASC")
    fun getUsers(): LiveData<List<User>>

    @Query("DELETE FROM user")
    fun deleteAll()
}