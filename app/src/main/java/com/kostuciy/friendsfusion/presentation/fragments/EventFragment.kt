package com.kostuciy.friendsfusion.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kostuciy.friendsfusion.R
import com.kostuciy.friendsfusion.databinding.FragmentEventBinding
import com.kostuciy.friendsfusion.databinding.FragmentGalleryBinding

class EventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentEventBinding.inflate(inflater, container, false)

        return binding.root
    }
}