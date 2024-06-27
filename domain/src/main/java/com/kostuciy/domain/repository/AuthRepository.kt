package com.kostuciy.domain.repository

import com.kostuciy.domain.model.Response
import com.kostuciy.domain.model.User

interface AuthRepository {

    suspend fun register(email: String, password: String): Response<Boolean>

    suspend fun signIn(email: String, password: String): Response<Boolean>

    suspend fun signOut(): Response<Boolean>

    suspend fun getAuthData(): Response<User?>
}