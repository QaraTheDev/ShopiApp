package com.example.shopiapp.presentation.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shopiapp.R
import com.example.shopiapp.databinding.FragmentLoginBinding
import com.example.shopiapp.exceptions.InvalidFirstNameException
import com.example.shopiapp.exceptions.InvalidPasswordException
import com.example.shopiapp.exceptions.LoginException
import com.example.shopiapp.model.AccountDto
import com.example.shopiapp.presentation.viewmodels.LoginViewModel
import com.example.shopiapp.util.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment @Inject constructor() :
    BindFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel by viewModels<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()
        setFlowCollectors()
    }

    private fun setFlowCollectors() {
        viewModel.stateFlow.onEach { handleState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.errorFlow.onEach { handleError(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.accountFlow.onEach { handleAccountFlow(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setClickListeners() {
        with(binding) {
            firstNameInputText.addTextChangedListener { clearAlarm() }
            passwordInputText.addTextChangedListener { clearAlarm() }
            loginButton.setOnClickListener { login() }
        }
    }

    private fun handleState(state: State) {
        when (state) {
            State.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            State.COMPLETE -> {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun handleError(exception: Exception) {
        when (exception) {
            is LoginException -> {
                binding.firstNameInputLayout.error = " "
                binding.passwordInputLayout.error = exception.message
            }
            is InvalidPasswordException -> {
                binding.passwordInputLayout.error = exception.message
            }
            is InvalidFirstNameException -> {
                binding.firstNameInputLayout.error = exception.message
            }
        }
    }

    private fun handleAccountFlow(account: AccountDto) {
        val args = Bundle().apply {
            putParcelable(resources.getString(R.string.account_key), account)
        }
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment, args)
    }

    private fun login() {
        clearAlarm()
        with(binding) {
            viewModel.login(firstNameInputText.text.toString(), passwordInputText.text.toString())
        }
    }

    private fun clearAlarm() {
        with(binding) {
            firstNameInputLayout.error = null
            passwordInputLayout.error = null
        }
    }
}