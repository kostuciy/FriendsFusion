package com.kostuciy.friendsfusion.viewmodel

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostuciy.domain.auth.model.AuthState
import com.kostuciy.domain.auth.model.Token
import com.kostuciy.domain.auth.model.User
import com.kostuciy.domain.auth.usecase.EditUserUseCase
import com.kostuciy.domain.auth.usecase.GetAuthDataUseCase
import com.kostuciy.domain.auth.usecase.RegisterUseCase
import com.kostuciy.domain.auth.usecase.SaveVKTokenToFirestoreUseCase
import com.kostuciy.domain.auth.usecase.SignInUseCase
import com.kostuciy.domain.auth.usecase.SignOutUseCase
import com.kostuciy.domain.auth.usecase.UpdateAuthDataUseCase
import com.kostuciy.domain.core.model.Result
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val getAuthDataUseCase: GetAuthDataUseCase,
        private val updateAuthDataUseCase: UpdateAuthDataUseCase,
        private val registerUseCase: RegisterUseCase,
        private val signInUseCase: SignInUseCase,
        private val signOutUseCase: SignOutUseCase,
        private val editUserUseCase: EditUserUseCase,
        private val saveVKTokenToFirestoreUseCase: SaveVKTokenToFirestoreUseCase,
    ) : ViewModel() {
        private var _state: MutableStateFlow<AuthState<User>> =
            MutableStateFlow(AuthState.Unauthenticated)
        val state: StateFlow<AuthState<User>>
            get() = _state

        private suspend fun setState(result: Flow<Result<Boolean>>) {
            result.collect {
                when (it) {
                    is Result.Error ->
                        _state.value =
                            AuthState.Error(
                                it.exception.message ?: it.exception.toString(),
                            )

                    is Result.Loading ->
                        _state.value =
                            AuthState.Loading

                    is Result.Success ->
                        coroutineScope {
                            delay(250)
                            getAuthDataUseCase.execute().collectLatest { user ->
                                _state.value =
                                    if (user == null) {
                                        AuthState.Unauthenticated
                                    } else {
                                        AuthState.Authenticated(user)
                                    }
                            }
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

        fun editUser(
            email: String,
            password: String,
            username: String,
        ) = viewModelScope.launch {
            setState(editUserUseCase.execute(email, password, username))
        }

        var vkAuthResultLauncher: ActivityResultLauncher<Collection<VKScope>>? = null
            private set

        fun setVkAuthResultLauncher(activity: ComponentActivity?) {
            if (vkAuthResultLauncher != null || activity == null) return

            vkAuthResultLauncher =
                VK.login(activity) { result ->
                    when (result) {
                        is VKAuthenticationResult.Success -> {
                            with(result.token) {
                                val vkToken =
                                    Token.VKToken(userId.value, accessToken)
                                saveVKTokenToFirestore(vkToken)
                            }
                        }

                        is VKAuthenticationResult.Failed -> {
                            saveVKTokenToFirestore(null)
                        }
                    }
                }
        }

        private fun saveVKTokenToFirestore(vkToken: Token.VKToken?) =
            viewModelScope.launch {
                setState(saveVKTokenToFirestoreUseCase.execute(vkToken))
            }
    }
