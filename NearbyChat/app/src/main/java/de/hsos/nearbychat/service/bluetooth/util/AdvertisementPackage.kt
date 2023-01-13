package de.hsos.nearbychat.service.bluetooth.util

import java.util.LinkedList

data class AdvertisementPackage(var id: Char? = null) {
    private val messageList: MutableList<Advertisement> = LinkedList()
    private var rawMessageBegin: String? = null
    private var rawMessageEnd: String? = null
    var size: Int = 2

    fun addAdvertisement(advertisement: Advertisement) {
        this.messageList.add(advertisement)
        this.size += advertisement.toString().length
    }

    fun addCutMessageBegin(message: String) {
        this.rawMessageBegin = message
        this.size += message.length
    }

    fun getRawMessageBegin(): String? = this.rawMessageBegin

    fun addCutMessageEnd(message: String) {
        this.rawMessageEnd = message
        this.size += message.length
    }

    fun getRawMessageEnd(): String? = this.rawMessageEnd


    fun getMessageList(): MutableList<Advertisement> = this.messageList


    override fun toString(): String {
        val stringBuilder = StringBuilder(this.id.toString())
            .append(":")
        if (this.rawMessageBegin != null) {
            stringBuilder.append(this.rawMessageBegin)
        }
        this.messageList.forEach { stringBuilder.append(it.toString()) }
        if (this.rawMessageEnd != null) {
            stringBuilder.append(this.rawMessageEnd)
        }
        return stringBuilder.toString()
    }

    companion object {

        fun toPackage(packageStr: String): AdvertisementPackage {
            val result = AdvertisementPackage()
            result.id = packageStr[0]
            var lastSeparator: Int = 2
            var nextSeparator: Int = packageStr.indexOf('}') + 1
            if (nextSeparator == 0) {
                nextSeparator = packageStr.length
            }
            while (nextSeparator != 0) {
                val msg = packageStr.substring(lastSeparator, nextSeparator)
                if (msg.contains('{') && msg.contains('}')) {
                    result.addAdvertisement(Advertisement.Builder().rawMessage(msg).build())
                } else {
                    result.addCutMessageBegin(msg)
                }
                lastSeparator = nextSeparator
                nextSeparator = packageStr.indexOf('}', lastSeparator) + 1

            }
            if (packageStr[packageStr.length - 1] != '}') {
                result.addCutMessageEnd(packageStr.substring(lastSeparator, packageStr.length))
            }
            return result
        }
    }
}