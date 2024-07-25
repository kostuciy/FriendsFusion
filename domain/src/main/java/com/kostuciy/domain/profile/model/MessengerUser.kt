package com.kostuciy.domain.profile.model

sealed class MessengerUser {
    data class VKUser(
        val id: Long,
        val name: String,
        val avatarUrl: String?,
    ) : MessengerUser()

//    TODO: change depending on api
    data class TelegramUser(
        val id: Long,
        val name: String,
    ) : MessengerUser()
}
