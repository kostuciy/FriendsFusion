package com.kostuciy.domain.profile.usecase

import com.kostuciy.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class GetProfileDataUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    fun execute() = repository.profileData
}