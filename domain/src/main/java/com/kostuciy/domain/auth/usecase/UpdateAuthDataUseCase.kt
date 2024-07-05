package com.kostuciy.domain.auth.usecase

import com.kostuciy.domain.auth.repository.AuthRepository
import javax.inject.Inject

class UpdateAuthDataUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend fun execute() = repository.updateAuthData()
}