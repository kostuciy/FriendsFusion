package com.kostuciy.domain.profile.model

sealed class Token {

    data class VKToken(
        val id: Long,
        val accessToken: String
    ) : Token()
}