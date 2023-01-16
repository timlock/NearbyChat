package de.hsos.nearbychat.app.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["address", "timeStamp", "isSelfAuthored"])
class Message(val address: String, val content: String, val timeStamp: Long) {
    var isReceived: Boolean = false
    var isSelfAuthored: Boolean = false

    override fun toString(): String {
        return "Message(address='$address', content='$content', timeStamp=$timeStamp, isReceived=$isReceived, isSelfAuthored=$isSelfAuthored)"
    }
}