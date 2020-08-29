package com.noto.app.sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.noto.databinding.FragmentSignUpBinding
import com.noto.app.util.snackbar
import com.noto.app.sign.SignUpFragmentDirections
import org.koin.android.viewmodel.ext.android.viewModel


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding

    private val viewModel by viewModel<SignSharedViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = this@SignUpFragment
            viewModel = this@SignUpFragment.viewModel
        }

        binding.btnSignUp.setOnClickListener {
            viewModel.createUser()
        }

        binding.tvSignIn.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())
        }

        binding.tvSkip.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionGlobalLibraryListFragment())
        }

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                when {
                    error.contains("Name", true) -> binding.tilName.error = error
                    error.contains("Email", true) -> binding.tilEmail.error = error
                    error.contains("Password", true) -> binding.tilPassword.error = error
                    else -> binding.root.snackbar(error)
                }
            }
        })

        viewModel.navigate.observe(viewLifecycleOwner, Observer { navigate ->
            navigate?.let {
                if (it) findNavController().navigate(SignUpFragmentDirections.actionGlobalLibraryListFragment())
            }
        })

        return binding.root
    }

}