package com.leafcode.petmint.pet

import android.os.Parcel
import android.os.Parcelable

//Data class for the Object Pet
data class Pet(
    val adNumber: Int = 1,
    val userID: String? = "jshahfjsaf",
    val adID: String? = "fjaskjfnas",
    val name: String? = "Charlie",
    val gender: Int = 0,
    /*0 -> MALE
    1-> FEMALE*/
    val animalType: String? = "Dog",
    /*0-> Dog
    1-> Cat
    2-> Bird
    3-> Other */
    val breed: String? = "Pitbull",
    val description: String? = "A very decent dog.",
    val mobileNumber: String? = "4526582658",
    val ownerName: String? = "Suresh",
    val userName: String? = "Suresh",
    val image1: String? = "jhfgasjkhfjkds",
    val image2: String? = "fbjbdskjbnfds",
    val image3: String? = "fjkabsdfbjdsa",
    val postingDate: String? = "fbkjdkjbf",
    val city: String? = "fjkbdfkjjsd",
    val state: String? = "fbesjkbefkjsnf",
    val country: String? = "fjksehejkfhs",
    val address: String? ="nfkeneskjfnkjesnf",
    val pincode: String? = "jsenekhekgs"
    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(adNumber)
        parcel.writeString(userID)
        parcel.writeString(adID)
        parcel.writeString(name)
        parcel.writeInt(gender)
        parcel.writeString(animalType)
        parcel.writeString(breed)
        parcel.writeString(description)
        parcel.writeString(mobileNumber)
        parcel.writeString(ownerName)
        parcel.writeString(userName)
        parcel.writeString(image1)
        parcel.writeString(image2)
        parcel.writeString(image3)
        parcel.writeString(postingDate)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeString(country)
        parcel.writeString(address)
        parcel.writeString(pincode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pet> {
        override fun createFromParcel(parcel: Parcel): Pet {
            return Pet(parcel)
        }

        override fun newArray(size: Int): Array<Pet?> {
            return arrayOfNulls(size)
        }
    }
}