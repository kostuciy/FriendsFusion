package com.kostuciy.data.profile.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kostuciy.data.profile.entity.MessengerUserEntity
import com.kostuciy.data.profile.entity.TokenEntity
import com.kostuciy.data.profile.entity.UserEntity
import com.kostuciy.data.profile.entity.UserWithMessengers
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Transaction
    @Query("SELECT * FROM users WHERE isCurrentUser=1")
    fun getCurrentUserFlow(): Flow<UserWithMessengers?>

    @Transaction
    @Query("SELECT * FROM users WHERE isCurrentUser=1")
    suspend fun getCurrentUser(): UserWithMessengers?

    @Query("SELECT * FROM tokens")
    fun getTokensFlow(): Flow<List<TokenEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTokens(tokens: List<TokenEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessengers(messengers: List<MessengerUserEntity>)
}
