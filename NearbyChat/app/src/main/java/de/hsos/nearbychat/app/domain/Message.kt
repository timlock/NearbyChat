package de.hsos.nearbychat.app.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Message(val address: String, val content: String, @PrimaryKey val timeStamp: Long) {
    var isReceived: Boolean = false
    var isSelfAuthored: Boolean = false
}