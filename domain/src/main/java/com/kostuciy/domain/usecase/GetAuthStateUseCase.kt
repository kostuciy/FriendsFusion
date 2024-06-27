package com.kostuciy.domain.usecase

import com.kostuciy.domain.repository.AuthRepository
import javax.inject.Inject

class GetAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend fun execute() = repository.getAuthData()
}