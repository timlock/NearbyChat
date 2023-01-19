package de.hsos.nearbychat.service.bluetooth.util

import de.hsos.nearbychat.common.domain.Message

class UnacknowledgedMessageBuffer {
    private var messageList: MutableSet<Message> = mutableSetOf()

    @Synchronized
    fun acknowledge(receiver: String, timestamp: Long): Boolean {
        val result = this.messageList.filter { it.address == receiver && it.timeStamp == timestamp }
        return if(result.isNotEmpty()){
            this.messageList.remove(result[0])
            true
        }else{
            false
        }
    }

    @Synchronized
    fun addMessages(messageList: List<Message>){
        this.messageList.addAll(messageList)
    }

    @Synchronized
    fun addMessage(message: Message){
        this.messageList.add(message)
    }

    @Synchronized
    fun getMessages() : List<Message> = this.messageList.toList()
}