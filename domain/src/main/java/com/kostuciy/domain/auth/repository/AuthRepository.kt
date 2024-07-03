package com.kostuciy.domain.auth.repository

import com.kostuciy.domain.auth.model.Response
import com.kostuciy.domain.auth.model.User
import com.kostuciy.domain.vk.model.VKUserToken

interface AuthRepository {
    suspend fun getAuthData(): Response<User?>
    suspend fun signUp(email: String, password: String, username: String): Response<User>
    suspend fun signIn(email: String, password: String): Response<User?>
    suspend fun signOut(): Response<Boolean>
    suspend fun editUser(email: String, password: String, username: String): Response<User>
    suspend fun saveVkTokenToFirebase(vkUserToken: VKUserToken): Response<User>
}