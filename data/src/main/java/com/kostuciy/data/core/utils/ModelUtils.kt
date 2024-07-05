package com.kostuciy.data.core.utils

import com.kostuciy.data.core.db.entity.MessengerType
import com.kostuciy.data.core.db.entity.MessengerUserEntity
import com.kostuciy.data.core.db.entity.TokenEntity
import com.kostuciy.data.core.db.entity.UserEntity
import com.kostuciy.domain.auth.model.Token
import com.kostuciy.domain.auth.model.User
import com.kostuciy.domain.core.model.MessengerUser

object ModelUtils {

    fun MessengerUser.toEntity(userId: String? = null) = when (this) {
        is MessengerUser.TelegramUser -> MessengerUserEntity(
            id, userId, name, MessengerType.TELEGRAM
        )

        is MessengerUser.VKUser -> MessengerUserEntity(
            id, userId, name, MessengerType.VK
        )
    }

    fun Token.toEntity() = when (this) {
        is Token.VKToken -> TokenEntity(id, accessToken, MessengerType.VK)
//        TODO: add telegram later
    }

    fun User.toEntity() =
        UserEntity(
            id, username, avatarUrl,
            profile?.email, isCurrentUser
        )

}