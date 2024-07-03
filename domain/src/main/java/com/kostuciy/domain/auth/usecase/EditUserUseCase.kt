package com.kostuciy.domain.auth.usecase

import com.kostuciy.domain.auth.repository.AuthRepository
import javax.inject.Inject

class EditUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend fun execute(
        email: String,
        password: String,
        username: String
    ) = repository.editUser(email, password, username)
}