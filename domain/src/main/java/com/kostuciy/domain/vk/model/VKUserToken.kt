package com.kostuciy.domain.vk.model

data class VKUserToken(
    val id: Long,
    val accessToken: String,
    val login: String?, // either phone, email or null
)
