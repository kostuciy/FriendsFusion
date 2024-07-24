package com.kostuciy.data.core.utils

import com.kostuciy.data.profile.entity.MessengerType
import com.kostuciy.data.profile.entity.MessengerUserEntity
import com.kostuciy.data.profile.entity.TokenEntity
import com.kostuciy.data.profile.entity.UserEntity
import com.kostuciy.domain.profile.model.MessengerUser
import com.kostuciy.domain.profile.model.Token
import com.kostuciy.domain.profile.model.User

object ModelUtils {
    fun MessengerUser.toEntity(userId: String? = null) =
        when (this) {
            is MessengerUser.TelegramUser ->
                MessengerUserEntity(
                    id,
                    userId,
                    name,
                    MessengerType.TELEGRAM,
                )

            is MessengerUser.VKUser ->
                MessengerUserEntity(
                    id,
                    userId,
                    name,
                    MessengerType.VK,
                )
        }

    fun Token.toEntity() =
        when (this) {
            is Token.VKToken -> TokenEntity(id, accessToken, MessengerType.VK)
//        TODO: add telegram later
        }

    fun User.toEntity() =
        UserEntity(
            id,
            username,
            avatarUrl,
            profile?.email,
            isCurrentUser,
        )
}
