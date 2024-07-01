package com.kostuciy.domain.repository

import com.kostuciy.domain.model.Response
import com.kostuciy.domain.model.User

interface AuthRepository {

    suspend fun getAuthData(): Response<User?>

    suspend fun signUp(email: String, password: String, username: String): Response<User>

    suspend fun signIn(email: String, password: String): Response<User?>

    suspend fun signOut(): Response<Boolean>

    suspend fun editUser(email: String, password: String, username: String): Response<User>
}