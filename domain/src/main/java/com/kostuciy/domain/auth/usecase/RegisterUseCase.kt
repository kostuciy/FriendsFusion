package com.kostuciy.domain.auth.usecase

import com.kostuciy.domain.auth.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend fun execute(
        email: String,
        password: String,
        username: String
    ) = repository.signUp(email, password, username)
}