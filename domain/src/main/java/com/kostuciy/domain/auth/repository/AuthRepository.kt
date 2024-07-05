package com.kostuciy.domain.auth.repository

import com.kostuciy.domain.auth.model.Token
import com.kostuciy.domain.core.model.Response
import com.kostuciy.domain.auth.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authData: Flow<User?>

    suspend fun updateAuthData(): Response<Boolean>
    suspend fun signUp(email: String, password: String, username: String): Response<Boolean>
    suspend fun signIn(email: String, password: String): Response<Boolean>
    suspend fun signOut(): Response<Boolean>
    suspend fun editUser(email: String, password: String, username: String): Response<Boolean>
    suspend fun saveVkToken(vkToken: Token.VKToken): Response<Boolean>
}