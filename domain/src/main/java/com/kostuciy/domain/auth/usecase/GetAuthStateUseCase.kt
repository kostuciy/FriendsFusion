package com.kostuciy.domain.auth.usecase

import com.kostuciy.domain.auth.repository.AuthRepository
import javax.inject.Inject

class GetAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend fun execute() = repository.getAuthData()
}