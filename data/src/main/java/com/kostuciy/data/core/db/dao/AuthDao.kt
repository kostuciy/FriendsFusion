package com.kostuciy.data.core.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kostuciy.data.core.db.entity.MessengerUserEntity
import com.kostuciy.data.core.db.entity.TokenEntity
import com.kostuciy.data.core.db.entity.UserEntity
import com.kostuciy.data.core.db.entity.UserWithMessengers
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {

    @Transaction
    @Query("SELECT * FROM users WHERE isCurrentUser=1")
    fun getCurrentUser(): Flow<UserWithMessengers?>

    @Query("SELECT * FROM tokens")
    fun getTokens(): Flow<List<TokenEntity>>

    //    TODO: implememnt for chats
//    @Transaction
//    @Query("SELECT * FROM users")
//    fun getUsers(): Flow<List<UserWithMessengers>>
//
//    @Transaction
//    @Query("SELECT * FROM users WHERE id=:id")
//    suspend fun getUserById(id: String): UserWithMessengers TODO: remove, needed in separate dao

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessengers(messengers: List<MessengerUserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTokens(tokens: List<TokenEntity>)

    @Query("DELETE FROM users WHERE isCurrentUser=1")
    suspend fun clearUser()

    @Query("DELETE FROM users_messengers")
    suspend fun clearMessengers()

    @Query("DELETE FROM tokens")
    suspend fun clearTokens()
}