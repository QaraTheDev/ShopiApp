package com.example.shopiapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.test.domain.entities.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class AccountSourceViewModel @Inject constructor() : ViewModel() {

    private var _account: Account? = null
    val account get() = _account!!

    fun setAccount(account: Account) {
        _account = account
    }
}