package com.kostuciy.friendsfusion.presentation.fragments

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
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
import com.kostuciy.friendsfusion.databinding.FragmentSignInBinding
import com.kostuciy.friendsfusion.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        val navController = findNavController()

        with(binding) {
            submit.setOnClickListener {
                val email = this.email.text.toString()
                val password = this.password.text.toString()
                viewModel.signIn(email, password)
            }

            signUp.setOnClickListener {
                navController.navigate(R.id.action_signInFragment_to_signUpFragment)
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
                        is AuthState.Authenticated -> navController.navigate(
                            R.id.action_signInFragment_to_profileFragment
                        )
                        is AuthState.Error -> with(binding) {
                            this.error.isVisible = true
                            this.error.text = state.message
                            progressBar.isVisible = false
                            submit.isEnabled = true
                        }
                        AuthState.Loading -> with(binding) {
                            progressBar.isVisible = true
                            submit.isEnabled = false
                        }
                        AuthState.Unauthenticated -> with(binding) {
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