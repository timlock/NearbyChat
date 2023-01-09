package de.hsos.nearbychat.app.domain

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.lang.Integer.max
import java.lang.Integer.min

@Entity
class Profile(@PrimaryKey val address: String) {
    lateinit var name: String
    lateinit var description: String
    var color: Int = 0
    var lastInteraction: Long = Long.MIN_VALUE
    @Ignore
    var unread: Boolean = false
    @Ignore
    var rssi: Int = Int.MIN_VALUE
    @Ignore
    var hopCount: Int = 0

    fun signalStrength0to4(): Int {
        var strength = rssi + 140
        strength -= hopCount * 20
        return min(max(strength / 60, 0), 4)
    }

}