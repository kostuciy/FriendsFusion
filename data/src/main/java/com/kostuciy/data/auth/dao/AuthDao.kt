package com.kostuciy.data.auth.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kostuciy.data.profile.entity.UserEntity

@Dao
interface AuthDao {
//    TODO: implememnt for chats
//    @Transaction
//    @Query("SELECT * FROM users")
//    fun getUsers(): Flow<List<UserWithMessengers>>
//
//    @Transaction
//    @Query("SELECT * FROM users WHERE id=:id")
//    suspend fun getUserById(id: String): UserWithMessengers TODO: remove, needed in separate dao
//
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users WHERE isCurrentUser=1")
    suspend fun clearUser()

    @Query("DELETE FROM users_messengers")
    suspend fun clearMessengers()

    @Query("DELETE FROM tokens")
    suspend fun clearTokens()
}
