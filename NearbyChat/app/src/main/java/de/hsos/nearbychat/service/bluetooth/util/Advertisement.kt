package de.hsos.nearbychat.service.bluetooth.util

import android.util.Log
import de.hsos.nearbychat.service.bluetooth.MessageType

class Advertisement private constructor(
    var type: Char? = null,
    var id: Char? = null,
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


    @Throws(IllegalStateException::class)
    override fun toString(): String {
        val builder: StringBuilder = StringBuilder()
            .append("{")
            .append(type)
            .append(":")
        when (type) {
            MessageType.NEIGHBOUR_MESSAGE.type -> {
                builder.append(hops)
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
            MessageType.ACKNOWLEDGE_MESSAGE.type -> {
                builder.append(id)
                    .append(';')
                    .append(nextHop)
                    .append(';')
                    .append(sender)
                    .append(';')
                    .append(receiver)
                    .append(';')
                    .append(timestamp)
            }
            MessageType.MESSAGE_MESSAGE.type -> {
                builder.append(id)
                    .append(';')
                    .append(address)
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
                throw IllegalStateException()
            }
        }
        builder.append("}")
        return builder.toString()
    }

    data class Builder(
        var type: Char? = null,
        var id: Char? = null,
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
        fun id(id: Char) = apply { this.id = id }
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
        fun timestamp(timestamp: Long) = apply {this.timestamp = timestamp}
        fun rawMessage(rawMessage: String) = apply {
            try {
                this.type = rawMessage[1]
                when (this.type) {
                    MessageType.MESSAGE_MESSAGE.type -> {
                        var lastSeparator: Int = rawMessage.indexOf(':') + 1
                        var nextSeparator: Int = rawMessage.indexOf(';')
                        this.id =
                            rawMessage.substring(lastSeparator, nextSeparator).toCharArray().first()
                        lastSeparator = ++nextSeparator
                        nextSeparator = rawMessage.indexOf(';', nextSeparator)
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
                    MessageType.ACKNOWLEDGE_MESSAGE.type -> {
                        var lastSeparator: Int = rawMessage.indexOf(':') + 1
                        var nextSeparator: Int = rawMessage.indexOf(';')
                        this.id = rawMessage.substring(lastSeparator, nextSeparator).toCharArray().first()
                        lastSeparator = ++nextSeparator
                        nextSeparator = rawMessage.indexOf(';', nextSeparator)
                        this.nextHop = rawMessage.substring(lastSeparator, nextSeparator)
                        lastSeparator = ++nextSeparator
                        nextSeparator = rawMessage.indexOf(';', nextSeparator)
                        this.sender = rawMessage.substring(lastSeparator, nextSeparator)
                        lastSeparator = ++nextSeparator
                        nextSeparator = rawMessage.indexOf('}')
                        this.receiver = rawMessage.substring(lastSeparator, nextSeparator)
                        lastSeparator = ++nextSeparator
                        nextSeparator = rawMessage.indexOf(';', nextSeparator)
                        this.timestamp = rawMessage.substring(lastSeparator, nextSeparator).toLong()
                    }
                    MessageType.NEIGHBOUR_MESSAGE.type -> {
                        var lastSeparator: Int = rawMessage.indexOf(':') + 1
                        var nextSeparator: Int = rawMessage.indexOf(';')
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
                    else -> Log.w(
                        TAG,
                        "toMessage: unknown message type in: $rawMessage"
                    )

                }
            } catch (e: IndexOutOfBoundsException) {
                Log.w(TAG, "toMessage: received incomplete message: $rawMessage")
            }
        }

        fun build() =
            Advertisement(
                type,
                id,
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