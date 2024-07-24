package com.kostuciy.friendsfusion.profile.viewmodel

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostuciy.domain.core.model.Result
import com.kostuciy.domain.profile.model.ProfileState
import com.kostuciy.domain.profile.model.Token
import com.kostuciy.domain.profile.model.User
import com.kostuciy.domain.profile.usecase.EditProfileUseCase
import com.kostuciy.domain.profile.usecase.GetProfileDataUseCase
import com.kostuciy.domain.profile.usecase.SaveTokenUseCase
import com.kostuciy.domain.profile.usecase.UpdateProfileDataUseCase
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
class ProfileViewModel @Inject constructor(
    private val editProfileUseCase: EditProfileUseCase,
    private val getProfileDataUseCase: GetProfileDataUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val updateProfileDataUseCase: UpdateProfileDataUseCase,
) : ViewModel() {
    private var _state: MutableStateFlow<ProfileState> =
        MutableStateFlow(ProfileState.Loading)
    val state: StateFlow<ProfileState>
        get() = _state

    var vkAuthResultLauncher: ActivityResultLauncher<Collection<VKScope>>? = null
        private set

    private suspend fun setState(result: Flow<Result<Boolean>>) {
        result.collect {
            when (it) {
                is Result.Loading ->
                    _state.value = ProfileState.Loading
                is Result.Error ->
                    _state.value =
                        ProfileState.Error(
                            it.exception.message ?: it.exception.toString(),
                        )
                is Result.Success ->
                    coroutineScope {
                        delay(250)
                        val profileData: Flow<User?> = getProfileDataUseCase.execute()
                        profileData.collectLatest { user ->
                            _state.value = ProfileState.Profile(user)
                        }
                    }
            }
        }
    }

    fun setVkAuthResultLauncher(activity: ComponentActivity?) {
        if (vkAuthResultLauncher != null || activity == null) return

        vkAuthResultLauncher =
            VK.login(activity) { result ->
                when (result) {
                    is VKAuthenticationResult.Success -> {
                        with(result.token) {
                            val vkToken =
                                Token.VKToken(userId.value, accessToken)
//                                TODO: save token to db
                        }
                    }

                    is VKAuthenticationResult.Failed -> {
//                            TODO: do something
                    }
                }
            }
    }

    fun editProfile(
        email: String,
        password: String,
        username: String,
    ) = viewModelScope.launch {
        setState(editProfileUseCase.execute(email, password, username))
    }

    fun saveToken(token: Token?) =
        viewModelScope.launch {
            setState(saveTokenUseCase.execute(token))
        }

    fun updateProfileData() =
        viewModelScope.launch {
            setState(updateProfileDataUseCase.execute())
        }

    fun signOut() {
        _state.value = ProfileState.Profile(null)
    }
}
