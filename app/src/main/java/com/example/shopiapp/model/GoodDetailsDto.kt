package com.example.shopiapp.model

import android.os.Parcel
import android.os.Parcelable
import com.test.domain.entities.GoodDetails
import com.example.shopiapp.exceptions.ParcelException

class GoodDetailsDto(
    override val name: String,
    override val description: String,
    override val rating: Double,
    override val reviewsCount: Int,
    override val price: Double,
    override val colors: List<String>,
    override val imageUrls: List<String>
) : GoodDetails, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: throw ParcelException(EXCEPTION_MESSAGE),
        parcel.readString() ?: throw ParcelException(EXCEPTION_MESSAGE),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.createStringArrayList() ?: throw ParcelException(EXCEPTION_MESSAGE),
        parcel.createStringArrayList() ?: throw ParcelException(EXCEPTION_MESSAGE)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeDouble(rating)
        parcel.writeInt(reviewsCount)
        parcel.writeDouble(price)
        parcel.writeStringList(colors)
        parcel.writeStringList(imageUrls)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GoodDetailsDto> {

        const val EXCEPTION_MESSAGE = "Parcel account exception"

        override fun createFromParcel(parcel: Parcel): GoodDetailsDto {
            return GoodDetailsDto(parcel)
        }

        override fun newArray(size: Int): Array<GoodDetailsDto?> {
            return arrayOfNulls(size)
        }
    }
}