package com.kostuciy.friendsfusion.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kostuciy.friendsfusion.R
import com.kostuciy.friendsfusion.databinding.FragmentSignInBinding
import com.kostuciy.friendsfusion.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = FragmentSignUpBinding.inflate(inflater, container, false)



        return view.root
    }
}