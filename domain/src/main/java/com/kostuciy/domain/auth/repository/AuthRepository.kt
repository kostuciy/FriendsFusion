package com.kostuciy.domain.auth.repository

import com.kostuciy.domain.core.model.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
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
}
