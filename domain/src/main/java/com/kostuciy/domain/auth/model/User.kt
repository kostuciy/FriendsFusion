package com.kostuciy.domain.auth.model

data class User(
    val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val linkedMessengerUsers: List<MessengerUser> = emptyList(),
    val profile: UserProfile? = null,
) {
    val isCurrentUser = profile != null
}

class UserProfile(
    val email: String,
    val tokens: List<Token> = emptyList(),
)
