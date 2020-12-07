package com.udacity.shoestore.screens.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.udacity.shoestore.R
import com.udacity.shoestore.databinding.FragmentLoginBinding
import com.udacity.shoestore.databinding.FragmentWelcomeBinding
import com.udacity.shoestore.screens.login.LoginFragmentDirections


class WelcomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentWelcomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)


        val args: WelcomeFragmentArgs by navArgs()

        binding.tvUser.text = args.username

        binding.btnGetStarted.setOnClickListener(
            Navigation.createNavigateOnClickListener(WelcomeFragmentDirections.actionWelcomeFragmentToInstructionFragment())
        )

        return binding.root
    }

}