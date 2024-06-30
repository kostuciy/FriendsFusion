package com.kostuciy.domain.repository

import com.kostuciy.domain.model.Response
import com.kostuciy.domain.model.User

interface AuthRepository {

    suspend fun getAuthData(): Response<User?>

    suspend fun register(email: String, password: String, username: String): Response<User>

    suspend fun signIn(email: String, password: String): Response<User?>

    suspend fun signOut(): Response<Boolean>
}