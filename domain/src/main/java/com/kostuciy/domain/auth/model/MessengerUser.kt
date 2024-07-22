package com.kostuciy.domain.auth.model

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
