package de.hsos.nearbychat.app.domain

import android.os.Parcel
import android.os.Parcelable
import java.time.Instant

data class Message(val content: String, val timeStamp: Long) :Parcelable {
    var isReceived: Boolean = false
    var isSelfAuthored: Boolean = false

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readLong()
    ) {
        isReceived = parcel.readByte() != 0.toByte()
        isSelfAuthored = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(content)
        parcel.writeLong(timeStamp)
        parcel.writeByte(if (isReceived) 1 else 0)
        parcel.writeByte(if (isSelfAuthored) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}