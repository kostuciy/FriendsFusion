package com.kostuciy.domain.usecase

import com.kostuciy.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend fun execute(
        email: String,
        password: String
    ) = repository.signIn(email, password)
}