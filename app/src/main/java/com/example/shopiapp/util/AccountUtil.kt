package com.example.shopiapp.util

import com.test.domain.entities.Account
import com.example.shopiapp.model.AccountDto
import javax.inject.Inject

class AccountUtil @Inject constructor() {

    fun convertAccountToDto(account: Account): AccountDto {
        return AccountDto(
            account.id,
            account.firstName,
            account.lastName,
            account.password,
            account.photoUrl,
            account.email,
            account.balance
        )
    }
}