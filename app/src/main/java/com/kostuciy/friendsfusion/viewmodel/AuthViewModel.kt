package com.kostuciy.friendsfusion.viewmodel

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostuciy.domain.auth.model.Response
import com.kostuciy.domain.auth.model.User
import com.kostuciy.domain.auth.model.AuthState
import com.kostuciy.domain.auth.usecase.EditUserUseCase
import com.kostuciy.domain.auth.usecase.GetAuthStateUseCase
import com.kostuciy.domain.auth.usecase.RegisterUseCase
import com.kostuciy.domain.auth.usecase.SaveVKTokenToFirestoreUseCase
import com.kostuciy.domain.auth.usecase.SignInUseCase
import com.kostuciy.domain.auth.usecase.SignOutUseCase
import com.kostuciy.domain.vk.model.VKUserToken
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
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
    private val editUserUseCase: EditUserUseCase,
    private val saveVKTokenToFirestoreUseCase: SaveVKTokenToFirestoreUseCase
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

    var vkAuthResultLauncher: ActivityResultLauncher<Collection<VKScope>>? = null
        private set

    fun setVkAuthResultLauncher(activity: ComponentActivity?) {
        if (vkAuthResultLauncher != null || activity == null) return

        vkAuthResultLauncher = VK.login(activity) { result ->
            when (result) {
                is VKAuthenticationResult.Success -> {
                    with(result.token) {
                        val vkUserToken =
                            VKUserToken(userId.value, accessToken, phone ?: email)
                        saveVKTokenToFirestore(vkUserToken)
                    }
                }

                is VKAuthenticationResult.Failed -> {
                    AuthState.Error(
                        result.exception.message ?: result.exception.toString()
                    )
                }
            }
        }
    }

    private fun saveVKTokenToFirestore(vkUserToken: VKUserToken) = viewModelScope.launch {
        _state.value = AuthState.Loading
        _state.value = saveVKTokenToFirestoreUseCase.execute(vkUserToken).let { response ->
            when (response) {
                is Response.Success -> AuthState.Authenticated(response.data)
                is Response.Failure -> AuthState.Error(
                    response.exception.message ?: response.exception.toString()
                )
            }
        }
    }


}