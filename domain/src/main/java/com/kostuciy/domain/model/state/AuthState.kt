package com.kostuciy.domain.model.state

import com.kostuciy.domain.model.User

data class AuthState(
    val isAuthenticated: Boolean = false,
    val user: User = User()
)