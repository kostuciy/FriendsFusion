package com.kostuciy.domain.auth.usecase

import com.kostuciy.domain.auth.repository.AuthRepository
import com.kostuciy.domain.vk.model.VKUserToken
import javax.inject.Inject


class SaveVKTokenToFirestoreUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend fun execute(vkUserToken: VKUserToken) =
        repository.saveVkTokenToFirebase(vkUserToken)
}