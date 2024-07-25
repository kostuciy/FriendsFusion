package com.kostuciy.domain.profile.usecase

import com.kostuciy.domain.vk.repository.VKRepository
import javax.inject.Inject

class GetVKMessengerUserUseCase @Inject constructor(
    private val repository: VKRepository,
) {
    suspend fun execute(id: Long) = repository.getProfileUser(id)
}
