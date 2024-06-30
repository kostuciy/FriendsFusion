package com.kostuciy.friendsfusion.presentation.fragments

import android.os.Bundle
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
        val binding = FragmentSignInBinding.inflate(inflater, container, false)

        val navController = findNavController()

        with(binding) {
            submit.setOnClickListener {
                val email = this.email.text.toString()
                val password = this.password.text.toString()
                viewModel.signIn(email, password)
            }
        }

//        TODO: redo after testing
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.testTextView.text = when (state) {
                        is AuthState.Authenticated -> "Auth: ${state.user.username}"
                        is AuthState.Error -> "Error: ${state.message}"
                        AuthState.Loading -> "Loading..."
                        AuthState.Unauthenticated -> "Unauth"
                    }                    }
                }
            }



        return binding.root
    }
}