package com.kostuciy.friendsfusion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostuciy.domain.model.Response
import com.kostuciy.domain.model.User
import com.kostuciy.domain.model.state.AuthState
import com.kostuciy.domain.usecase.EditUserUseCase
import com.kostuciy.domain.usecase.GetAuthStateUseCase
import com.kostuciy.domain.usecase.RegisterUseCase
import com.kostuciy.domain.usecase.SignInUseCase
import com.kostuciy.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val editUserUseCase: EditUserUseCase
) : ViewModel() {

    private var _state: MutableStateFlow<AuthState<User>> =
        MutableStateFlow(AuthState.Unauthenticated)
    val state: StateFlow<AuthState<User>>
        get() = _state

    init {
        getAuthData()
    }

    private fun getAuthData() = viewModelScope.launch {
        _state.value = AuthState.Loading
        _state.value = getAuthStateUseCase.execute().let { response ->
            when (response) {
                is Response.Success -> {
                    response.data?.let {
                        AuthState.Authenticated(it)
                    } ?: AuthState.Unauthenticated
                }
                is Response.Failure -> AuthState.Error(
                    response.exception.message ?: response.exception.toString()
                )
            }
        }
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
            _state.value = AuthState.Loading
            _state.value = signInUseCase.execute(email, password).let { response ->
                when (response) {
                    is Response.Success -> {
                        response.data?.let {
                            AuthState.Authenticated(it)
                        } ?: AuthState.Unauthenticated
                    }

                    is Response.Failure -> AuthState.Error(
                        response.exception.message ?: response.exception.toString()
                    )
                }
            }
        }

    fun signOut() = viewModelScope.launch {
        _state.value = AuthState.Loading
        _state.value = signOutUseCase.execute().let { response ->
            when (response) {
                is Response.Success -> AuthState.Unauthenticated
                is Response.Failure -> AuthState.Error(
                    response.exception.message ?: response.exception.toString()
                )
            }
        }
    }

    fun signUp(email: String, password: String, username: String) = viewModelScope.launch {
        _state.value = AuthState.Loading
        _state.value = registerUseCase.execute(email, password, username).let { response ->
            when (response) {
                is Response.Success -> AuthState.Authenticated(response.data)

                is Response.Failure -> AuthState.Error(
                    response.exception.message ?: response.exception.toString()
                )
            }
        }
    }

    fun editUser(email: String, password: String, username: String) = viewModelScope.launch {
        _state.value = AuthState.Loading
        _state.value = editUserUseCase.execute(email, password, username).let { response ->
            when (response) {
                is Response.Success -> AuthState.Authenticated(response.data)
                is Response.Failure -> AuthState.Error(
                    response.exception.message ?: response.exception.toString()
                )
            }
        }
    }
}