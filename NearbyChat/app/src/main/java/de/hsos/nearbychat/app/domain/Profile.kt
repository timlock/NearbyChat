package de.hsos.nearbychat.app.domain

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable

data class Profile(val macAddress: String) : Parcelable {
    lateinit var name: String
    lateinit var description: String
    var color: Int = 0
    var isAvailable: Boolean = false
    var neighbors: List<Profile> = mutableListOf() // streichen
    var messages: List<Message> = mutableListOf()
    var rssi: Int = 0 // hinzufügen zeitstempel letzte nachricht & hop count

    constructor(parcel: Parcel) : this(parcel.readString()!!) {
        name = parcel.readString()!!
        description = parcel.readString()!!
        color = parcel.readInt()
        isAvailable = parcel.readByte() != 0.toByte()
        neighbors = parcel.createTypedArrayList(CREATOR)!!
        messages = parcel.createTypedArrayList(Message)!!
        rssi = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(macAddress)
        parcel.writeString(name)
        parcel.writeInt(color)
        parcel.writeString(description)
        parcel.writeByte(if (isAvailable) 1 else 0)
        parcel.writeTypedList(neighbors)
        parcel.writeTypedList(messages)
        parcel.writeInt(rssi)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Profile> {
        override fun createFromParcel(parcel: Parcel): Profile {
            return Profile(parcel)
        }

        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }
    }
}