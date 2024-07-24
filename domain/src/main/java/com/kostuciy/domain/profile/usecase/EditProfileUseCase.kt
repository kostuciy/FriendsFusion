package com.kostuciy.domain.profile.usecase

import com.kostuciy.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class EditProfileUseCase
    @Inject
    constructor(
        private val repository: ProfileRepository,
    ) {
        suspend fun execute(
            email: String,
            password: String,
            username: String,
        ) = repository.editProfile(email, password, username)
    }
