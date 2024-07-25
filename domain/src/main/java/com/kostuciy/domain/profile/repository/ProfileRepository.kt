package com.kostuciy.domain.profile.repository

import com.kostuciy.domain.core.model.Result
import com.kostuciy.domain.profile.model.Token
import com.kostuciy.domain.profile.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    val profileData: Flow<User?>

    suspend fun updateProfileData(): Flow<Result<Boolean>>

    suspend fun editProfile(
        email: String,
        password: String,
        username: String,
    ): Flow<Result<Boolean>>

    suspend fun saveToken(token: Token): Flow<Result<Boolean>>

    suspend fun checkVKTokenExists(): Boolean

    suspend fun checkTelegramTokenExists(): Boolean
}
