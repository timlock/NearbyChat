package de.hsos.nearbychat.service.bluetooth.util

import android.util.Log
import de.hsos.nearbychat.common.domain.Message

class UnacknowledgedMessageBuffer {
    private var TAG: String = UnacknowledgedMessageBuffer::class.java.simpleName
    private var messageList: MutableSet<Message> = mutableSetOf()

    @Synchronized
    fun acknowledge(receiver: String, timestamp: Long): Boolean {
        val result = this.messageList.filter { it.address == receiver && it.timeStamp == timestamp }
        return if(result.isNotEmpty()){
            Log.d(TAG, "acknowledge() called with: message acknowledged for: receiver = $receiver, timestamp = $timestamp")
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