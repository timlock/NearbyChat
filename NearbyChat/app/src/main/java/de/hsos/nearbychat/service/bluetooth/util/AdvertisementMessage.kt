package de.hsos.nearbychat.service.bluetooth.util

import de.hsos.nearbychat.service.bluetooth.MessageType

class AdvertisementMessage private constructor(
    var type: MessageType? = null,
    var id: Char? = null,
    var maxHop: String? = null,
    var macAddress: String? = null,
    var name: String? = null,
    var description: String? = null,
    var color: Int? = null,
    var message: String? = null
) {

    @Throws(IllegalStateException::class)
    override fun toString(): String {
        val builder: StringBuilder = StringBuilder()
            .append("{")
            .append(type)
            .append(":")
        when (type) {
            MessageType.MESSAGE_MESSAGE -> {
                builder.append(maxHop)
                    .append(macAddress)
                    .append(name)
                    .append(description)
                    .append(color)
            }
            MessageType.ACKNOWLEDGE_MESSAGE -> {
                builder.append(id)
                    .append(macAddress)
            }
            MessageType.NEIGHBOUR_MESSAGE -> {
                builder.append(id)
                    .append(macAddress)
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
        var maxHop: String? = null,
        var macAddress: String? = null,
        var name: String? = null,
        var description: String? = null,
        var color: Int? = null,
        var message: String? = null
    ) {
        fun type(type: MessageType) = apply { this.type = type }
        fun id(id: Char) = apply { this.id = id }
        fun maxHop(maxHop: String) = apply { this.maxHop = maxHop }
        fun macAddress(macAddress: String) = apply { this.macAddress = macAddress }
        fun name(name: String) = apply { this.name = name }
        fun description(description: String) = apply { this.description = description }
        fun color(color: Int) = apply { this.color = color }
        fun message(message: String) = apply { this.message = message }
        fun build() =
            AdvertisementMessage(type, id, maxHop, macAddress, name, description, color, message)
    }
}