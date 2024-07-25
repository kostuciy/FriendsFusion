package com.kostuciy.domain.vk.repository

import com.kostuciy.domain.core.model.Result
import com.kostuciy.domain.profile.model.MessengerUser
import kotlinx.coroutines.flow.Flow

interface VKRepository {
    suspend fun getProfileUser(id: Long): Flow<Result<Boolean>>

    suspend fun getUsers(ids: List<Long>): Flow<Result<List<MessengerUser.VKUser>>>
}
