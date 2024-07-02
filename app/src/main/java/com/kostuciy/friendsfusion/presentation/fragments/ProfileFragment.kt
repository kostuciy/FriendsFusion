package com.kostuciy.friendsfusion.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kostuciy.domain.model.state.AuthState
import com.kostuciy.friendsfusion.R
import com.kostuciy.friendsfusion.databinding.FragmentProfileBinding
import com.kostuciy.friendsfusion.utils.AppUtils
import com.kostuciy.friendsfusion.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )

        with(binding) {
            signOut.setOnClickListener {
                viewModel.signOut()
            }

            submitChanges.setOnClickListener {
                AppUtils.hideKeyboard(activity)

                val email = email.text.toString()
                val password = password.text.toString()
                val username = username.text.toString()
                viewModel.editUser(email, password, username)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is AuthState.Authenticated -> updateViews(binding, state)
                        is AuthState.Error -> with(binding) {
                            error.text = state.message
                            error.isVisible = true
                            progressBar.isVisible = false
                            submitChanges.isEnabled = true
                            signOut.isEnabled = true
                        }
                        AuthState.Loading -> with(binding) {
                            this.submitChanges.isEnabled = false
                            this.signOut.isEnabled = false
                            this.progressBar.isVisible = true
                        }
                        AuthState.Unauthenticated -> findNavController().navigate(
                            R.id.action_profileFragment_to_signInFragment
                        )
                    }
                }
            }
        }

        return binding.root
    }

    private fun updateViews(binding: FragmentProfileBinding, state: AuthState.Authenticated) {
        with(binding) {
            progressBar.isVisible = false
            this.submitChanges.isEnabled = true
            this.signOut.isEnabled = true
            error.isVisible = false
            profileTitle.text = getString(
                R.string.profile_title, state.user.username
            )
            username.setText(state.user.username)
            email.setText(state.user.email)
        }
    }
}