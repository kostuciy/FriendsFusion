package com.kostuciy.domain.profile.usecase

import com.kostuciy.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class CheckTelegramTokenExistsUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend fun execute() = repository.checkTelegramTokenExists()
}
