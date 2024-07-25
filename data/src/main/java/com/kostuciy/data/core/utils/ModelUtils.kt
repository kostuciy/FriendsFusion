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
                    avatarUrl,
                )
        }

    fun Token.toEntity() =
        when (this) {
            is Token.VKToken -> TokenEntity(MessengerType.VK, id, accessToken)
//        TODO: change telegram later
            is Token.TelegramToken -> TokenEntity(MessengerType.TELEGRAM, id, accessToken)
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
