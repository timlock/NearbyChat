package de.hsos.nearbychat.app.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Message(val address: String, val content: String, val timeStamp: Long) {
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
    var isReceived: Boolean = false
    var isSelfAuthored: Boolean = false
}