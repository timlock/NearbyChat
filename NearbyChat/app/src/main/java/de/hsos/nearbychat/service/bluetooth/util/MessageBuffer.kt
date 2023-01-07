package de.hsos.nearbychat.service.bluetooth.util

class MessageBuffer {
    private val buffer: HashMap<Char, String> = HashMap()

    fun add(packageID: Char, message: String): String? {
        var entry: String? = this.buffer.remove(packageID)
        return if(entry == null ){
            if( message.contains('{')) {
                this.buffer[this.getNextId(packageID)] = message
            }
            null
        }else{
            entry += message
            if(message.contains('}')){
                entry
            }else {
                this.buffer[this.getNextId(packageID)] = entry
                null
            }
        }
    }

    private fun getNextId(id: Char) :Char = AtomicIdGenerator(id).next()
}