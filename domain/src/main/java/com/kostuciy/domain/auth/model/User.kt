package com.kostuciy.domain.auth.model

import com.kostuciy.domain.vk.model.VKUserToken

data class User(
    val id: String,
    val email: String,
    val username: String,
    val vkUserToken: VKUserToken? = null
)