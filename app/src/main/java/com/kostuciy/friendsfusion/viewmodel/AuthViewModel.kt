package com.kostuciy.friendsfusion.viewmodel

import androidx.annotation.MainThread
import androidx.annotation.UiThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostuciy.domain.model.Response
import com.kostuciy.domain.model.User
import com.kostuciy.domain.model.state.AuthState
import com.kostuciy.domain.model.state.UIState
import com.kostuciy.domain.repository.AuthRepository
import com.kostuciy.domain.usecase.GetAuthStateUseCase
import com.kostuciy.domain.usecase.RegisterUseCase
import com.kostuciy.domain.usecase.SignInUseCase
import com.kostuciy.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    var uiState: UIState = UIState.Loading
        private set

    var authState: AuthState = AuthState()
        private set

    init {
        getAuthData()
    }

    private fun getAuthData() = viewModelScope.launch {
        uiState = UIState.Loading
        uiState = getAuthStateUseCase.execute().let { response ->
            when (response) {
                is Response.Success -> {
                    authState = AuthState(response.data == null, response.data ?: User())
                    UIState.Showing
                }
                is Response.Failure -> UIState.Error(response.exception)
            }
        }
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
            uiState = UIState.Loading
            uiState = signInUseCase.execute(email, password).let { response ->
                when (response) {
                    is Response.Success -> {
                        if (!response.data) {
                            AuthState(false, User())
                        } else withContext(Dispatchers.Main) { getAuthData() } // TODO: return user, not boolean
                        UIState.Showing
                    }
                    is Response.Failure -> UIState.Error(response.exception)
                }
            }
        }

    fun signOut() {
        TODO()
    }

    fun register(email: String, password: String) {
        TODO()
    }





}