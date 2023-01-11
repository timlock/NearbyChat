package de.hsos.nearbychat.service.bluetooth.util

import java.util.LinkedList

class UnacknowledgedMessageList {
    private var messageList: MutableList<Advertisement> = LinkedList()

    @Synchronized
    fun acknowledge(receiver: String, timestamp: Long): Boolean {
        val result = this.messageList.filter { it.receiver == receiver && it.timestamp == timestamp }
        return if(result.isNotEmpty()){
            this.messageList.remove(result[0])
            true
        }else{
            false
        }
    }

    @Synchronized
    fun addMessages(messageList: List<Advertisement>){
        this.messageList.addAll(messageList)
    }

    @Synchronized
    fun getMessages() : List<Advertisement> = this.messageList.toList()
}