package com.kostuciy.friendsfusion.presentation.fragments

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kostuciy.domain.model.state.AuthState
import com.kostuciy.friendsfusion.R
import com.kostuciy.friendsfusion.databinding.FragmentSignUpBinding
import com.kostuciy.friendsfusion.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)

        val navController = findNavController()

        with(binding) {
            submit.setOnClickListener {
                val email = this.email.text.toString()
                val password = this.password.text.toString()
                val passwordConfirmation = this.passwordConfirm.text.toString()
                val username = this.username.text.toString()

                if (passwordConfirmation == password)
                    viewModel.signUp(email, password, username)
            }

            showPassword.setOnCheckedChangeListener { button, isChecked ->
                password.transformationMethod =
                    if (isChecked) HideReturnsTransformationMethod.getInstance()
                    else PasswordTransformationMethod.getInstance()
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is AuthState.Authenticated -> navController.popBackStack()
                        is AuthState.Error -> "Error: ${state.message}"
                        AuthState.Loading -> "Loading..."
                        AuthState.Unauthenticated ->  "Unauth"
                    }
                }
            }
        }

        return binding.root
    }
}