package de.hsos.nearbychat.service.bluetooth.util

import android.util.Log
import de.hsos.nearbychat.service.bluetooth.MessageType

class AdvertisementMessage private constructor(
    var type: MessageType? = null,
    var id: Char? = null,
    var hops: Int? = null,
    var rssi: Int? = null,
    var address: String? = null,
    var sender: String? = null,
    var receiver: String? = null,
    var name: String? = null,
    var description: String? = null,
    var color: Int? = null,
    var message: String? = null,
) {
    val TAG: String = AdvertisementMessage::class.java.simpleName

    fun decrementHop() = apply { this.hops = this.hops?.minus(1) }


    @Throws(IllegalStateException::class)
    override fun toString(): String {
        val builder: StringBuilder = StringBuilder()
            .append("{")
            .append(type?.type)
            .append(":")
        when (type) {
            MessageType.NEIGHBOUR_MESSAGE -> {
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
            MessageType.ACKNOWLEDGE_MESSAGE -> {
                builder.append(id)
                    .append(';')
                    .append(address)
                    .append(';')
                    .append(sender)
                    .append(';')
                    .append(receiver)
            }
            MessageType.MESSAGE_MESSAGE -> {
                builder.append(id)
                    .append(';')
                    .append(address)
                    .append(';')
                    .append(sender)
                    .append(';')
                    .append(receiver)
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
        var type: MessageType? = null,
        var id: Char? = null,
        var hops: Int? = null,
        var rssi: Int? = null,
        var address: String? = null,
        var sender: String? = null,
        var receiver: String? = null,
        var name: String? = null,
        var description: String? = null,
        var color: Int? = null,
        var message: String? = null,
    ) {
        private val TAG: String = AdvertisementMessage.Builder::class.java.simpleName
        fun type(type: MessageType) = apply { this.type = type }
        fun id(id: Char) = apply { this.id = id }
        fun hops(hops: Int) = apply { this.hops = hops }
        fun rssi(rssi: Int) = apply { this.rssi = rssi }
        fun address(address: String) = apply { this.address = address }
        fun sender(sender: String) = apply { this.sender = sender }
        fun receiver(receiver: String) = apply { this.receiver = receiver }
        fun name(name: String) = apply { this.name = name }
        fun description(description: String) = apply { this.description = description }
        fun color(color: Int) = apply { this.color = color }
        fun message(message: String) = apply { this.message = message }
        fun rawMessage(rawMessage: String) = apply {
            try {
                this.type = MessageType.values().first { it.type == rawMessage[1] }
                when (this.type) {
                    MessageType.MESSAGE_MESSAGE -> {
                        var lastSeparator: Int = rawMessage.indexOf(':') + 1
                        var nextSeparator: Int = rawMessage.indexOf(';')
                        this.id =
                            rawMessage.substring(lastSeparator, nextSeparator).toCharArray().first()
                        lastSeparator = ++nextSeparator
                        nextSeparator = rawMessage.indexOf(';', nextSeparator)
                        this.address = rawMessage.substring(lastSeparator, nextSeparator)
                        lastSeparator = ++nextSeparator
                        nextSeparator = rawMessage.indexOf(';', nextSeparator)
                        this.sender = rawMessage.substring(lastSeparator, nextSeparator)
                        lastSeparator = ++nextSeparator
                        nextSeparator = rawMessage.indexOf(';', nextSeparator)
                        this.receiver = rawMessage.substring(lastSeparator, nextSeparator)
                        lastSeparator = ++nextSeparator
                        nextSeparator = rawMessage.indexOf('}')
                        this.message = rawMessage.substring(lastSeparator, nextSeparator)
                    }
                    MessageType.ACKNOWLEDGE_MESSAGE -> {
                        var lastSeparator: Int = rawMessage.indexOf(':') + 1
                        var nextSeparator: Int = rawMessage.indexOf(';')
                        this.id =
                            rawMessage.substring(lastSeparator, nextSeparator).toCharArray().first()
                        lastSeparator = ++nextSeparator
                        nextSeparator = rawMessage.indexOf(';', nextSeparator)
                        lastSeparator = ++nextSeparator
                        nextSeparator = rawMessage.indexOf(';', nextSeparator)
                        this.address = rawMessage.substring(lastSeparator, nextSeparator)
                        this.sender = rawMessage.substring(lastSeparator, nextSeparator)
                        lastSeparator = ++nextSeparator
                        nextSeparator = rawMessage.indexOf('}')
                        this.receiver = rawMessage.substring(lastSeparator, nextSeparator)
                    }
                    MessageType.NEIGHBOUR_MESSAGE -> {
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
            AdvertisementMessage(
                type,
                id,
                hops,
                rssi,
                address,
                sender,
                receiver,
                name,
                description,
                color,
                message
            )
    }
}