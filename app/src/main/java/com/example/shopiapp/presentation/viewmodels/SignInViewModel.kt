package com.example.shopiapp.presentation.viewmodels

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.domain.exceptions.AccountAlreadyExistException
import com.test.domain.model.requests.SignInObject
import com.test.domain.usecases.GetCurrentUserUseCase
import com.test.domain.usecases.SignInUseCase
import com.example.shopiapp.exceptions.*
import com.example.shopiapp.model.AccountDto
import com.example.shopiapp.util.State
import com.example.shopiapp.util.AccountUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
open class SignInViewModel @Inject constructor() : ViewModel() {

    @Inject
    protected lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Inject
    protected lateinit var signInUseCase: SignInUseCase

    @Inject
    protected lateinit var accountUtil: AccountUtil

    private val _stateFlow = MutableStateFlow(State.COMPLETE)
    val stateFlow get() = _stateFlow.asStateFlow()

    private val _errorChannel = Channel<Exception>()
    val errorFlow get() = _errorChannel.receiveAsFlow()

    private val _accountChannel = Channel<AccountDto>()
    val accountFlow get() = _accountChannel.receiveAsFlow()

    fun signIn(firstName: String, lastName: String?, email: String) {
        val photoUri = "${Environment.getExternalStorageDirectory()}/user_icons"
        viewModelScope.launch {
            try {
                _stateFlow.value = State.LOADING
                val isFirstNameValid = isFirstNameValid(firstName)
                val isEmailValid = isEmailValid(email)
                if (isFirstNameValid && isEmailValid) {
                    val account = signInUseCase.execute(
                        SignInObject(
                            firstName,
                            lastName,
                            email,
                            photoUri
                        )
                    ).account ?: throw SignInException("Account not created try later again")
                    _accountChannel.send(
                        accountUtil.convertAccountToDto(account)
                    )
                }
            } catch (ex: Exception) {
//            todo exception handle
                if (ex is AccountAlreadyExistException) _errorChannel.send(
                    SignInException(
                        ex.message ?: "Account not created try later again"
                    )
                )
                _errorChannel.send(ex)
            } finally {
                _stateFlow.value = State.COMPLETE
            }
        }
    }

    fun signInGoogle() {
        viewModelScope.launch {
            try {
                throw GoogleAuthorisedException("Google authorise error")
            } catch (ex: Exception) {
//            todo exception handle
                _errorChannel.send(ex)
            }
        }
    }

    fun signInApple() {
        viewModelScope.launch {
            try {
                throw AppleAuthorisedException("Apple authorise error")
            } catch (ex: Exception) {
//            todo exception handle
                _errorChannel.send(ex)
            }
        }
    }

    fun getCurrentAccount() {
        runBlocking {
            try {
                _stateFlow.value = State.LOADING
//                val account = getCurrentUserUseCase.execute().account
//                _accountChannel.send(
//                    accountUtil.convertAccountToDto(account ?: return@runBlocking)
//                )
            } catch (ex: Exception) {
//            todo exception handle
            } finally {
                _stateFlow.value = State.COMPLETE
            }
        }
    }

    private fun isEmailValid(email: String?): Boolean {
        return try {
            val isValid =
                email != null
                        && email.isNotEmpty()
                        && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            viewModelScope.launch {
                if (!isValid) _errorChannel.send(
                    InvalidEmailException("Invalid email")
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