package com.kostuciy.domain.auth.model

sealed class Token {

    data class VKToken(
        val id: Long,
        val accessToken: String
    ) : Token()
}