package com.kostuciy.friendsfusion.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostuciy.domain.auth.model.AuthState
import com.kostuciy.domain.auth.usecase.RegisterUseCase
import com.kostuciy.domain.auth.usecase.SignInUseCase
import com.kostuciy.domain.auth.usecase.SignOutUseCase
import com.kostuciy.domain.auth.usecase.UpdateAuthDataUseCase
import com.kostuciy.domain.core.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val updateAuthDataUseCase: UpdateAuthDataUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {
    private var _state: MutableStateFlow<AuthState> =
        MutableStateFlow(AuthState.Unauthenticated)
    val state: StateFlow<AuthState>
        get() = _state

    private suspend fun setState(result: Flow<Result<Boolean>>) {
        result.collect {
            _state.value =
                when (it) {
                    is Result.Error ->
                        AuthState.Error(
                            it.exception.message ?: it.exception.toString(),
                        )

                    is Result.Loading -> AuthState.Loading

                    is Result.Success ->
                        if (!it.data) {
                            AuthState.Unauthenticated
                        } else {
                            AuthState.Authenticated
                        }
                }
        }
    }

    init {
        updateAuthData()
    }

    private fun updateAuthData() =
        viewModelScope.launch {
            setState(updateAuthDataUseCase.execute())
        }

    fun signIn(
        email: String,
        password: String,
    ) = viewModelScope.launch {
        setState(signInUseCase.execute(email, password))
    }

    fun signOut() =
        viewModelScope.launch {
            setState(signOutUseCase.execute())
        }

    fun signUp(
        email: String,
        password: String,
        username: String,
    ) = viewModelScope.launch {
        setState(registerUseCase.execute(email, password, username))
    }
}
