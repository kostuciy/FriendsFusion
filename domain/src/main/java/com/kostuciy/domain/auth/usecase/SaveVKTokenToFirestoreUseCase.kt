package com.kostuciy.domain.auth.usecase

import com.kostuciy.domain.auth.model.Token
import com.kostuciy.domain.auth.repository.AuthRepository
import javax.inject.Inject

class SaveVKTokenToFirestoreUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        suspend fun execute(vkToken: Token.VKToken?) = repository.saveVkToken(vkToken)
    }
