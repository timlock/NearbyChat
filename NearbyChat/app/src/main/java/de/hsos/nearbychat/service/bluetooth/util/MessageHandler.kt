package de.hsos.nearbychat.service.bluetooth.util

import android.util.Log
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class MessageHandler(
    private val broadCaster: Broadcaster,
    private val period: Long,
    private val sizeLimit: Int
) {
    private val TAG: String = MessageHandler::class.java.simpleName
    private var messageExecutor: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor()
    private var isActive: Boolean = false
    private var messageQueue: MutableList<String> = LinkedList()
    private var idGenerator: AtomicIdGenerator = AtomicIdGenerator()

    @Synchronized
    fun start(): Boolean {
        return if (this.isActive) {
            Log.d(TAG, "start: could not start is already active")
            false
        } else {
            Log.d(TAG, "start: ")
            this.messageExecutor.scheduleAtFixedRate(
                this::broadcast,
                0,
                this.period,
                TimeUnit.MILLISECONDS
            )
            true
        }
    }

    fun stop() {
        if (this.isActive) {
            Log.d(TAG, "stop: ")
            this.messageExecutor.shutdown()
        } else {
            Log.d(TAG, "stop: could not stop is inactive")
        }

    }

    private fun broadcast() {
        if (this.messageQueue.isEmpty()) {
            return
        } else {
            val packageBuilder: StringBuilder = StringBuilder()
                .append(this.idGenerator.next())
                .append(':')
            while (packageBuilder.length < this.sizeLimit && this.messageQueue.isNotEmpty()) {
                val msg = this.messageQueue.removeFirst()
                if(msg.length + packageBuilder.length > this.sizeLimit){
                    packageBuilder.append(msg.substring(0,this.sizeLimit - packageBuilder.length))
                    this.messageQueue.add(0,msg.substring(this.sizeLimit - packageBuilder.length))
                }else{
                    packageBuilder.append(msg)
                }

            }
            if(packageBuilder.length > 2) {
                this.broadCaster.send(packageBuilder.toString())
            }
        }
    }

    fun send(message: AdvertisementMessage) {
        Log.d(TAG, "send: $message")
        this.messageQueue.add(message.toString())
    }
}