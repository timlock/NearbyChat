package de.hsos.nearbychat.service.bluetooth.util

class AtomicIdGenerator(private var id: Char = '0' - 1) {
    @Synchronized
    fun next(): Char {
        when (this.id) {
            '9' -> this.id = 'A'
            'Z' -> this.id = 'a'
            'z' -> this.id = '0'
            else -> this.id++
        }
        return this.id
    }

    @Synchronized
    fun setID(id: Char) {
        this.id = id
    }

    companion object {
        fun idIsValid(messageID: Char): Boolean {
            if (messageID in '0'..'9') return true
            if (messageID in 'A'..'Z') return true
            if (messageID in 'a'..'z') return true
            else return false
        }
    }
}