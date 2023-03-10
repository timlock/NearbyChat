package de.hsos.nearbychat.service.bluetooth.util

import android.util.Log
import de.hsos.nearbychat.service.bluetooth.AdvertisementType

class Advertisement private constructor(
    var type: Char? = null,
    var hops: Int? = null,
    var nextHop: String? = null,
    var rssi: Int? = null,
    var address: String? = null,
    var sender: String? = null,
    var receiver: String? = null,
    var name: String? = null,
    var description: String? = null,
    var color: Int? = null,
    var message: String? = null,
    var timestamp: Long? = null,
) {
    val TAG: String = Advertisement::class.java.simpleName

    fun decrementHop() = apply { this.hops = this.hops?.minus(1) }


    override fun toString(): String {
        val builder: StringBuilder = StringBuilder()
            .append("{")
            .append(type)
            .append(":")
        when (type) {
            AdvertisementType.NEIGHBOUR_ADVERTISEMENT.type -> {
                builder.append(sender)
                    .append(';')
                    .append(hops)
                    .append(';')
                    .append(rssi)
                    .append(';')
                    .append(address)
                    .append(';')
                    .append(name)
                    .append(';')
                    .append(description)
                    .append(';')
                    .append(color)
            }
            AdvertisementType.ACKNOWLEDGE_ADVERTISEMENT.type -> {
                builder
                    .append(nextHop)
                    .append(';')
                    .append(sender)
                    .append(';')
                    .append(receiver)
                    .append(';')
                    .append(timestamp)
            }
            AdvertisementType.MESSAGE_ADVERTISEMENT.type -> {
                builder
                    .append(nextHop)
                    .append(';')
                    .append(sender)
                    .append(';')
                    .append(receiver)
                    .append(';')
                    .append(timestamp)
                    .append(';')
                    .append(message)
            }
            else -> {
                Log.w(TAG, "toString: corrupted package")
                return ""
            }
        }
        builder.append("}")
        return builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Advertisement) return false

        if (type != other.type) return false
        if (hops != other.hops) return false
        if (nextHop != other.nextHop) return false
        if (rssi != other.rssi) return false
        if (address != other.address) return false
        if (sender != other.sender) return false
        if (receiver != other.receiver) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (color != other.color) return false
        if (message != other.message) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type?.hashCode() ?: 0
        result = 31 * result + (hops ?: 0)
        result = 31 * result + (nextHop?.hashCode() ?: 0)
        result = 31 * result + (rssi ?: 0)
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + (sender?.hashCode() ?: 0)
        result = 31 * result + (receiver?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (color ?: 0)
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (timestamp?.hashCode() ?: 0)
        return result
    }


    data class Builder(
        var type: Char? = null,
        var hops: Int? = null,
        var nextHop: String? = null,
        var rssi: Int? = null,
        var address: String? = null,
        var sender: String? = null,
        var receiver: String? = null,
        var name: String? = null,
        var description: String? = null,
        var color: Int? = null,
        var message: String? = null,
        var timestamp: Long? = null
    ) {
        private val TAG: String = Advertisement.Builder::class.java.simpleName
        fun type(type: Char) = apply { this.type = type }
        fun hops(hops: Int) = apply { this.hops = hops }
        fun nextHop(nextHop: String) = apply { this.nextHop = nextHop }
        fun rssi(rssi: Int) = apply { this.rssi = rssi }
        fun address(address: String) = apply { this.address = address }
        fun sender(sender: String) = apply { this.sender = sender }
        fun receiver(receiver: String) = apply { this.receiver = receiver }
        fun name(name: String) = apply { this.name = name }
        fun description(description: String) = apply { this.description = description }
        fun color(color: Int) = apply { this.color = color }
        fun message(message: String) = apply { this.message = message }
        fun timestamp(timestamp: Long) = apply { this.timestamp = timestamp }
        fun rawMessage(rawMessage: String) = apply {
            try {
                this.type = rawMessage[1]
                when (this.type) {
                    AdvertisementType.MESSAGE_ADVERTISEMENT.type -> {
                        this.parseMessage(rawMessage)
                    }
                    AdvertisementType.ACKNOWLEDGE_ADVERTISEMENT.type -> {
                        parseAcknowledgement(rawMessage)
                    }
                    AdvertisementType.NEIGHBOUR_ADVERTISEMENT.type -> {
                        parseNeighbour(rawMessage)
                    }
                    else -> Log.w(
                        TAG,
                        "rawMessage: unknown message type in: $rawMessage"
                    )

                }
            } catch (e: Exception) {
                Log.w(TAG, "rawMessage: received incomplete message: $rawMessage")
            }
        }


        @Throws(Exception::class)
        private fun parseMessage(rawMessage: String) {
            var lastSeparator: Int = rawMessage.indexOf(':') + 1
            var nextSeparator: Int = rawMessage.indexOf(';')
            this.nextHop = rawMessage.substring(lastSeparator, nextSeparator)
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf(';', nextSeparator)
            this.sender = rawMessage.substring(lastSeparator, nextSeparator)
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf(';', nextSeparator)
            this.receiver = rawMessage.substring(lastSeparator, nextSeparator)
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf(';', nextSeparator)
            this.timestamp = rawMessage.substring(lastSeparator, nextSeparator).toLong()
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf('}')
            this.message = rawMessage.substring(lastSeparator, nextSeparator)
        }

        @Throws(Exception::class)
        private fun parseAcknowledgement(rawMessage: String) {
            var lastSeparator: Int = rawMessage.indexOf(':') + 1
            var nextSeparator: Int = rawMessage.indexOf(';')
            this.nextHop = rawMessage.substring(lastSeparator, nextSeparator)
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf(';', nextSeparator)
            this.sender = rawMessage.substring(lastSeparator, nextSeparator)
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf(';', nextSeparator)
            this.receiver = rawMessage.substring(lastSeparator, nextSeparator)
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf('}')
            this.timestamp = rawMessage.substring(lastSeparator, nextSeparator).toLong()
        }

        @Throws(Exception::class)
        private fun parseNeighbour(rawMessage: String) {
            var lastSeparator: Int = rawMessage.indexOf(':') + 1
            var nextSeparator: Int = rawMessage.indexOf(';')
            this.sender = rawMessage.substring(lastSeparator, nextSeparator)
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf(';', nextSeparator)
            this.hops = rawMessage.substring(lastSeparator, nextSeparator).toInt()
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf(';', nextSeparator)
            this.rssi = rawMessage.substring(lastSeparator, nextSeparator).toInt()
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf(';', nextSeparator)
            this.address = rawMessage.substring(lastSeparator, nextSeparator)
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf(';', nextSeparator)
            this.name = rawMessage.substring(lastSeparator, nextSeparator)
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf(';', nextSeparator)
            this.description = rawMessage.substring(lastSeparator, nextSeparator)
            lastSeparator = ++nextSeparator
            nextSeparator = rawMessage.indexOf('}')
            this.color = rawMessage.substring(lastSeparator, nextSeparator).toInt()
        }


        fun build() =
            Advertisement(
                type,
                hops,
                nextHop,
                rssi,
                address,
                sender,
                receiver,
                name,
                description,
                color,
                message,
                timestamp
            )
    }
}