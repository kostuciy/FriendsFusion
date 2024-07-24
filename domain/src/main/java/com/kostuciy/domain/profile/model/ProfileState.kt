package com.kostuciy.domain.profile.model

sealed class ProfileState {
    data object Loading : ProfileState()

    data class Error(
        val message: String,
    ) : ProfileState()

    data class Profile(
        val user: User?,
    ) : ProfileState()
}
