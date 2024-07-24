package com.kostuciy.friendsfusion.profile.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kostuciy.domain.profile.model.ProfileState
import com.kostuciy.friendsfusion.R
import com.kostuciy.friendsfusion.auth.viewmodel.AuthViewModel
import com.kostuciy.friendsfusion.core.utils.AppUtils
import com.kostuciy.friendsfusion.databinding.FragmentProfileBinding
import com.kostuciy.friendsfusion.profile.viewmodel.ProfileViewModel
import com.vk.api.sdk.auth.VKScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel.updateProfileData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding =
            FragmentProfileBinding.inflate(
                inflater,
                container,
                false,
            )

        with(binding) {
            signOut.setOnClickListener {
                profileViewModel.signOut()
            }

            submitChanges.setOnClickListener {
                AppUtils.hideKeyboard(activity)

                val email = email.text.toString()
                val password = password.text.toString()
                val username = username.text.toString()
                profileViewModel.editProfile(email, password, username)
            }

            vkAuthenticate.setOnClickListener {
                profileViewModel.vkAuthResultLauncher?.launch(
                    arrayListOf(VKScope.WALL, VKScope.PHOTOS),
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.state.collect { state ->
                    when (state) {
                        is ProfileState.Profile -> {
                            if (state.user != null) {
                                updateViews(binding, state)
                            } else {
                                authViewModel.signOut()
                                findNavController().navigate(
                                    R.id.action_profileFragment_to_signInFragment,
                                )
                            }
                        }

                        is ProfileState.Error ->
                            with(binding) {
                                error.text = state.message
                                error.isVisible = true
                                progressBar.isVisible = false
                                submitChanges.isEnabled = true
                                signOut.isEnabled = true
                            }
                        is ProfileState.Loading ->
                            with(binding) {
                                this.submitChanges.isEnabled = false
                                this.signOut.isEnabled = false
                                this.progressBar.isVisible = true
                            }
                    }
                }
            }
        }

        return binding.root
    }

    private fun updateViews(
        binding: FragmentProfileBinding,
        state: ProfileState.Profile,
    ) {
        if (state.user == null) return
        with(binding) {
            vkAuthenticate.isEnabled =
                state.user!!
                    .profile!!
                    .tokens
                    .isEmpty() // TODO: remove test
            progressBar.isVisible = false
            submitChanges.isEnabled = true
            signOut.isEnabled = true
            error.isVisible = false
            profileTitle.text =
                getString(
                    R.string.profile_title,
                    state.user!!.username,
                )
            username.setText(state.user!!.username)
            email.setText(state.user!!.profile!!.email)
        }
    }
}
