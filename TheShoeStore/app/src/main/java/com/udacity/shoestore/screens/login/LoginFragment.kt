package com.udacity.shoestore.screens.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.udacity.shoestore.R
import com.udacity.shoestore.databinding.FragmentLoginBinding
import timber.log.Timber

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.loginViewModel = viewModel

        binding.btnLogin.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                LoginFragmentDirections.actionLoginFragmentToWelcomeFragment(viewModel.email.value.toString())
            )
        )

        binding.btnCreate.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                LoginFragmentDirections.actionLoginFragmentToWelcomeFragment(
                    viewModel.email.value.toString()
                )
            )
        )
        return binding.root
    }

}