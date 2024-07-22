package com.kostuciy.domain.auth.repository

import com.kostuciy.domain.auth.model.Token
import com.kostuciy.domain.auth.model.User
import com.kostuciy.domain.core.model.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authData: Flow<User?>

    suspend fun updateAuthData(): Flow<Result<Boolean>>

    suspend fun signUp(
        email: String,
        password: String,
        username: String,
    ): Flow<Result<Boolean>>

    suspend fun signIn(
        email: String,
        password: String,
    ): Flow<Result<Boolean>>

    suspend fun signOut(): Flow<Result<Boolean>>

    suspend fun editUser(
        email: String,
        password: String,
        username: String,
    ): Flow<Result<Boolean>>

    suspend fun saveVkToken(vkToken: Token.VKToken?): Flow<Result<Boolean>>
}
