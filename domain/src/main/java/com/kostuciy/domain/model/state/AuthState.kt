package com.kostuciy.domain.model.state

import com.kostuciy.domain.model.User

sealed class AuthState<out T> {
    data object Loading : AuthState<Nothing>()
    data class Error(val message: String) : AuthState<Nothing>()
    data class Authenticated(val user: User) : AuthState<User>()
    data object Unauthenticated : AuthState<Nothing>()
}