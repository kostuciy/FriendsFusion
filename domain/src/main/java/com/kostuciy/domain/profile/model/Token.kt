package com.kostuciy.domain.profile.model

sealed class Token {
    data class VKToken(
        val id: Long,
        val accessToken: String,
    ) : Token()

    //    TODO: change depending on TDLib
    data class TelegramToken(
        val id: Long,
        val accessToken: String,
    ) : Token()
}
