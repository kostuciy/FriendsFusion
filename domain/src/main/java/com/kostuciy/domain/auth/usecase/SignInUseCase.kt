package com.kostuciy.domain.auth.usecase

import com.kostuciy.domain.auth.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend fun execute(
        email: String,
        password: String
    ) = repository.signIn(email, password)
}