package com.example.shopiapp.model

import android.os.Parcel
import android.os.Parcelable
import com.test.domain.entities.AccountDto
import com.example.shopiapp.exceptions.ParcelException

class AccountDto(
    override val id: Int,
    override val firstName: String,
    override val lastName: String?,
    override val password: String,
    override val photoUrl: String,
    override val email: String,
    override val balance: Int
) : Account, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: throw ParcelException(EXCEPTION_MESSAGE),
        parcel.readString(),
        parcel.readString() ?: throw ParcelException(EXCEPTION_MESSAGE),
        parcel.readString() ?: throw ParcelException(EXCEPTION_MESSAGE),
        parcel.readString() ?: throw ParcelException(EXCEPTION_MESSAGE),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(password)
        parcel.writeString(photoUrl)
        parcel.writeString(email)
        parcel.writeInt(balance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AccountDto> {

        const val EXCEPTION_MESSAGE = "Parcel account exception"

        override fun createFromParcel(parcel: Parcel): AccountDto {
            return AccountDto(parcel)
        }

        override fun newArray(size: Int): Array<AccountDto?> {
            return arrayOfNulls(size)
        }
    }
}