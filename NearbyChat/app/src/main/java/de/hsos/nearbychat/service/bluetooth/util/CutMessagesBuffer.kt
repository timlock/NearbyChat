package de.hsos.nearbychat.service.bluetooth.util

class CutMessagesBuffer {
    private val bufferMap: HashMap<String, Pair<Char, String>> = HashMap()


    fun add(sender: String, packageID: Char, message: String): String? {
        val buffer = this.bufferMap.remove(sender)
        if (buffer == null) {
            if (message.contains('{')) {
                val newID = this.getNextId(packageID)
                this.bufferMap[sender] = Pair(newID, message)
            }
            return null
        } else {
            if (buffer.first == packageID) {
                val newEntry = buffer.second + message
                val newID = this.getNextId(packageID)
                if (newEntry.contains('{') && newEntry.contains('}')) {
                    return newEntry
                } else {
                    this.bufferMap[sender] = Pair(newID, newEntry)
                    return null
                }
            } else if (message.contains('{')) {
                this.bufferMap[sender] = Pair(packageID, message)
            }
            return null
        }
    }

    private fun getNextId(id: Char): Char {
        var newID = AtomicIdGenerator(id).next()
        while (newID.code % 2 != 0){
            newID = AtomicIdGenerator(newID).next()
        }
        return newID
    }
}