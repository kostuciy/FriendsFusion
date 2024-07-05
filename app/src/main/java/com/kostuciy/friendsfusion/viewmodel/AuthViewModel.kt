package com.kostuciy.friendsfusion.viewmodel

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostuciy.domain.core.model.Response
import com.kostuciy.domain.auth.model.User
import com.kostuciy.domain.auth.model.AuthState
import com.kostuciy.domain.auth.model.Token
import com.kostuciy.domain.auth.usecase.EditUserUseCase
import com.kostuciy.domain.auth.usecase.GetAuthDataUseCase
import com.kostuciy.domain.auth.usecase.UpdateAuthDataUseCase
import com.kostuciy.domain.auth.usecase.RegisterUseCase
import com.kostuciy.domain.auth.usecase.SaveVKTokenToFirestoreUseCase
import com.kostuciy.domain.auth.usecase.SignInUseCase
import com.kostuciy.domain.auth.usecase.SignOutUseCase
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAuthDataUseCase: GetAuthDataUseCase,
    private val updateAuthDataUseCase: UpdateAuthDataUseCase,
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

    private fun changeState(loading: Boolean = false, exception: Exception? = null) {
        viewModelScope.launch {
            val data: Flow<User?> = getAuthDataUseCase.execute()
            data.collect { user ->
                _state.value = when {
                    loading -> AuthState.Loading
                    user != null -> AuthState.Authenticated(user)
                    exception != null -> AuthState.Error(
                        exception.message ?: exception.toString()
                    )

                    else -> AuthState.Unauthenticated
                }
            }
        }
    }


    init {
        getAuthData()
    }

    private fun getAuthData() = viewModelScope.launch {
        changeState(loading = true)
        val exception = updateAuthDataUseCase.execute().let {
            if (it is Response.Failure) it.exception else null
        }
        changeState(exception = exception)
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
        changeState(loading = true)
        val exception = signInUseCase.execute(email, password).let {
            if (it is Response.Failure) it.exception else null
        }
        changeState(exception = exception)
    }

    fun signOut() = viewModelScope.launch {
        changeState(loading = true)
        val exception = signOutUseCase.execute().let {
            if (it is Response.Failure) it.exception else null
        }
        changeState(exception = exception)
    }

    fun signUp(email: String, password: String, username: String) = viewModelScope.launch {
        changeState(loading = true)
        val exception = registerUseCase.execute(email, password, username).let {
            if (it is Response.Failure) it.exception else null
        }
        changeState(exception = exception)
    }

    fun editUser(email: String, password: String, username: String) = viewModelScope.launch {
        changeState(loading = true)
        val exception = editUserUseCase.execute(email, password, username).let {
            if (it is Response.Failure) it.exception else null
        }
        changeState(exception = exception)
    }

    var vkAuthResultLauncher: ActivityResultLauncher<Collection<VKScope>>? = null
        private set

    fun setVkAuthResultLauncher(activity: ComponentActivity?) {
        if (vkAuthResultLauncher != null || activity == null) return

        vkAuthResultLauncher = VK.login(activity) { result ->
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

    private fun saveVKTokenToFirestore(vkToken: Token.VKToken?) = viewModelScope.launch {
        changeState(loading = true)

        if (vkToken == null)
            changeState(exception = NullPointerException())
        else {
            val exception = saveVKTokenToFirestoreUseCase.execute(vkToken).let {
                if (it is Response.Failure) it.exception else null
            }
            changeState(exception = exception)
        }
    }


}