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
    var isUnread: Boolean = false
    @Ignore
    var rssi: Int = Int.MIN_VALUE
    @Ignore
    var hopCount: Int = 0 // max is 10

    fun getSignalStrength0to4(): Int {
        return min(max(getSignalStrength() / 15, 0), 4)
    }

    fun getSignalStrength(): Int {
        var strength = rssi + 100
        strength -= (9 - hopCount) * 2
        return strength
    }

    fun updateSignal(profile: Profile) {
        rssi = profile.rssi
        hopCount = profile.hopCount
    }

    fun updateReceivedData(profile: Profile) {
        name = profile.name
        description = profile.description
        color = profile.color
        rssi = profile.rssi
        hopCount = profile.hopCount
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Profile

        if (address != other.address) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (color != other.color) return false
        if (lastInteraction != other.lastInteraction) return false
        if (isUnread != other.isUnread) return false
        if (rssi != other.rssi) return false
        if (hopCount != other.hopCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + color
        result = 31 * result + lastInteraction.hashCode()
        result = 31 * result + isUnread.hashCode()
        result = 31 * result + rssi
        result = 31 * result + hopCount
        return result
    }

    override fun toString(): String {
        return "Profile(address='$address', name='$name', description='$description', color=$color, lastInteraction=$lastInteraction, isUnread=$isUnread, rssi=$rssi, hopCount=$hopCount)"
    }
}