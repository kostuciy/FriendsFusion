package com.kostuciy.data.vk.repository

import com.kostuciy.data.core.utils.FlowUtils.asResult
import com.kostuciy.data.core.utils.ModelUtils.toEntity
import com.kostuciy.data.profile.dao.ProfileDao
import com.kostuciy.domain.core.model.Result
import com.kostuciy.domain.profile.model.MessengerUser
import com.kostuciy.domain.vk.repository.VKRepository
import com.vk.api.sdk.VK
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.users.UsersService
import com.vk.sdk.api.users.dto.UsersFieldsDto
import com.vk.sdk.api.users.dto.UsersUserFullDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VKRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
//    private val vkDao: VKDao
) : VKRepository {
    override suspend fun getProfileUser(id: Long): Flow<Result<Boolean>> =
        flow {
            val vkMessengerUser =
                withContext(Dispatchers.IO) {
                    VK
                        .executeSync(
                            UsersService().usersGet(
                                fields = listOf(UsersFieldsDto.PHOTO_200),
                                userIds = listOf(UserId(id)),
                            ),
                        ).first()
                        .let {
                            MessengerUser.VKUser(
                                it.id.value,
                                "${it.firstName} ${it.lastName}",
                                it.photo200,
                            )
                        }
                }

            val currentUserId = profileDao.getCurrentUser()?.user?.id
            if (currentUserId == null) {
                emit(false)
                return@flow
            }

            profileDao.insertMessengers(
                listOf(vkMessengerUser.toEntity(currentUserId)),
            )

            emit(true)
        }.asResult()

    override suspend fun getUsers(ids: List<Long>): Flow<Result<List<MessengerUser.VKUser>>> =
        flow {
            val vkIds = ids.map { UserId(it) }
            val vkUsers: List<UsersUserFullDto> = VK.executeSync(UsersService().usersGet(vkIds))

            emit(
                vkUsers.map {
                    MessengerUser.VKUser(
                        it.id.value,
                        "${it.firstName} ${it.lastName}",
                        it.photo200,
                    )
                },
            )
        }.asResult()
}
