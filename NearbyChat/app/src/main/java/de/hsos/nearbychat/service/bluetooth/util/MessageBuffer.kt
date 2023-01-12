package de.hsos.nearbychat.service.bluetooth.util

class MessageBuffer {
    private val buffer: HashMap<Char, String> = HashMap()

    fun add(packageID: Char, message: String): String? {
        var entry: String? = message
        var firstPackageId = packageID
        var lastPackageId = packageID
        this.buffer[packageID] = message
        while (entry != null && entry[0] != '{') {
            firstPackageId = AtomicIdGenerator.getPrevious(firstPackageId)
            entry = this.buffer[firstPackageId]
        }
        if (entry == null) {
            return null
        }
        entry = message
        while (entry != null && entry[entry.length - 1] != '}') {
            lastPackageId = this.getNextId(lastPackageId)
            entry = this.buffer[lastPackageId]
        }
        if (entry == null) {
            return null
        }
        val stringBuilder = StringBuilder()
        var index = firstPackageId
        while (index != lastPackageId) {
            stringBuilder.append(this.buffer.remove(index))
            index = this.getNextId(index)
        }
        stringBuilder.append(this.buffer.remove(lastPackageId))
        return stringBuilder.toString()
    }


//
//    fun add(packageID: Char, message: String): String? {
//        var entry: String? = this.buffer.remove(packageID)
//        var nextId = this.getNextId(packageID)
//        var nextEntry = this.buffer.remove(nextId)
//        if (entry == null && nextEntry == null) {
//            this.buffer[packageID] = message
//            return null
//        } else if (entry != null && nextEntry == null) {
//            entry += message
//            if (entry.contains('{') && entry.contains('}')) {
//                return entry
//            }else{
//                this.buffer[nextId] = entry
//                return null
//            }
//        }else if(entry == null){
//            entry = message + nextEntry
//            if (entry.contains('{') && entry.contains('}')) {
//                return entry
//            }else{
//                this.buffer[this.getNextId(nextId)] = entry
//                return null
//            }
//        }else{
//            entry += message + nextEntry
//            if (entry.contains('{') && entry.contains('}')) {
//                return entry
//            }else{
//                this.buffer[this.getNextId(nextId)] = entry
//                return null
//            }
//        }
//    }


//    fun add(packageID: Char, message: String): String? {
//        if (message.contains('{') && message.contains('}')) {
//            return message
//        } else {
//            var entry: String? = this.buffer.remove(packageID)
//            return if (entry == null) {
//                this.buffer[this.getNextId(packageID)] = message
//                null
//            }  else {
//                entry += message
//                if (entry.contains('{') && entry.contains('}')) {
//                    entry
//                } else {
//                    val nextID = this.getNextId(packageID)
//                    val nextEntry: String? = this.buffer.remove(nextID)
//                    if (nextEntry == null) {
//                        this.buffer[nextID] = entry
//                        null
//                    } else {
//                        entry += nextEntry
//                        if (entry.contains('}')) {
//                            entry
//                        } else {
//                            this.buffer[this.getNextId(nextID)] = entry
//                            null
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun getNextId(id: Char): Char = AtomicIdGenerator(id).next()
}