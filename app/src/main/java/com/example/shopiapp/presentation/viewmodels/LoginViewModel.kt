package com.example.shopiapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.domain.model.requests.LoginObject
import com.test.domain.usecases.LoginUseCase
import com.example.shopiapp.exceptions.InvalidFirstNameException
import com.example.shopiapp.exceptions.InvalidPasswordException
import com.example.shopiapp.exceptions.LoginException
import com.example.shopiapp.model.AccountDto
import com.example.shopiapp.util.State
import com.example.shopiapp.util.AccountUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class LoginViewModel @Inject constructor() : ViewModel() {

    @Inject
    protected lateinit var loginUseCase: LoginUseCase

    @Inject
    protected lateinit var accountUtil: AccountUtil

    private val _stateFlow = MutableStateFlow(State.COMPLETE)
    val stateFlow get() = _stateFlow.asStateFlow()

    private val _errorChannel = Channel<Exception>()
    val errorFlow get() = _errorChannel.receiveAsFlow()

    private val _accountChannel = Channel<AccountDto>()
    val accountFlow get() = _accountChannel.receiveAsFlow()

    fun login(firstName: String, password: String) {
        viewModelScope.launch {
            try {
                _stateFlow.value = State.LOADING
                val isFirstNameValid = isFirstNameValid(firstName)
//                val isPasswordValid = isPasswordValid(password)
                val isPasswordValid = true
                if (isFirstNameValid && isPasswordValid) {
                    val account = loginUseCase.execute(
                        LoginObject(
                            firstName,
                            password
                        )
                    ).account ?: throw LoginException("Invalid login or password")
                    _accountChannel.send(
                        accountUtil.convertAccountToDto(account)
                    )
                }
            } catch (ex: Exception) {
//            todo exception handle
                _errorChannel.send(ex)
            } finally {
                _stateFlow.value = State.COMPLETE
            }
        }
    }

    private fun isPasswordValid(password: String?): Boolean {
        return try {
            val isValid =
                password != null
                        && password.isNotEmpty()
                        && password.isNotBlank()
                        && !password.contains(' ')
            viewModelScope.launch {
                if (!isValid) _errorChannel.send(
                    InvalidPasswordException("Invalid password")
                )
            }
            isValid
        } catch (ex: Exception) {
            false
        }
    }

    private fun isFirstNameValid(firstName: String?): Boolean {
        return try {
            val isValid =
                firstName != null
                        && firstName.isNotEmpty()
                        && firstName.isNotBlank()
                        && !firstName.contains(' ')
            viewModelScope.launch {
                if (!isValid)
                    _errorChannel.send(InvalidFirstNameException("Unable value for first name"))
            }
            isValid
        } catch (ex: Exception) {
            false
        }
    }
}