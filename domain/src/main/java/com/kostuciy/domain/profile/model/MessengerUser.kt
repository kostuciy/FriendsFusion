package com.kostuciy.domain.profile.model

sealed class MessengerUser {
    //    TODO: change depending on api
    data class VKUser(
        val id: Long,
        val name: String,
    ) : MessengerUser()

//    TODO: change depending on api
    data class TelegramUser(
        val id: Long,
        val name: String,
    ) : MessengerUser()
}
