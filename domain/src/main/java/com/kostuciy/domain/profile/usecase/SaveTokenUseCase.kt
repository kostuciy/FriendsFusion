package com.kostuciy.domain.profile.usecase

import com.kostuciy.domain.profile.model.Token
import com.kostuciy.domain.profile.repository.ProfileRepository
import javax.inject.Inject

class SaveTokenUseCase
    @Inject
    constructor(
        private val repository: ProfileRepository,
    ) {
        suspend fun execute(token: Token?) = repository.saveToken(token)
    }
