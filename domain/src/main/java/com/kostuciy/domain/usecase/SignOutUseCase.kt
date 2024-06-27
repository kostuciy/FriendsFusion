package com.kostuciy.domain.usecase

import com.kostuciy.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend fun execute() = repository.signOut()
}