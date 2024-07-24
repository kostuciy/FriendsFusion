package com.kostuciy.friendsfusion.auth.presentation

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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
import com.kostuciy.domain.auth.model.AuthState
import com.kostuciy.friendsfusion.auth.viewmodel.AuthViewModel
import com.kostuciy.friendsfusion.core.utils.AppUtils
import com.kostuciy.friendsfusion.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class
SignUpFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)

        val navController = findNavController()

        with(binding) {
            submit.setOnClickListener {
                AppUtils.hideKeyboard(activity)

                val email = email.text.toString()
                val password = password.text.toString()
                val passwordConfirmation = passwordConfirm.text.toString()
                val username = username.text.toString()

                if (passwordConfirmation == password) {
                    viewModel.signUp(email, password, username)
                } else {
                    passwordConfirm.text.clear()
                }
            }

            showPassword.setOnCheckedChangeListener { button, isChecked ->
                password.transformationMethod =
                    if (isChecked) {
                        HideReturnsTransformationMethod.getInstance()
                    } else {
                        PasswordTransformationMethod.getInstance()
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is AuthState.Authenticated -> navController.popBackStack()
                        is AuthState.Error ->
                            with(binding) {
                                error.isVisible = true
                                error.text = state.message
                                progressBar.isVisible = false
                                submit.isEnabled = true
                            }
                        AuthState.Loading ->
                            with(binding) {
                                progressBar.isVisible = true
                                submit.isEnabled = false
                                error.isVisible = false
                            }
                        AuthState.Unauthenticated ->
                            with(binding) {
                                progressBar.isVisible = false
                                submit.isEnabled = true
                            }
                    }
                }
            }
        }

        return binding.root
    }
}
