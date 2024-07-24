package com.kostuciy.domain.auth.model

sealed class AuthState {
    data object Loading : AuthState()

    data class Error(
        val message: String,
    ) : AuthState()

    data object Authenticated : AuthState()

    data object Unauthenticated : AuthState()
}
