package de.hsos.nearbychat.service.bluetooth.advertise

import android.util.Log
import de.hsos.nearbychat.service.bluetooth.Advertiser
import de.hsos.nearbychat.service.bluetooth.util.Advertisement
import de.hsos.nearbychat.service.bluetooth.util.AdvertisementPackage
import de.hsos.nearbychat.service.bluetooth.util.AtomicIdGenerator
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class AdvertisementExecutor(
    private val broadCaster: Advertiser,
    val period: Long,
    private val sizeLimit: Int,
    private val advertisementQueue: AdvertisementQueue,
) {
    private val TAG: String = AdvertisementExecutor::class.java.simpleName
    private var scheduledExecutor: ScheduledExecutorService =
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
            Log.d(TAG, "start: rate ${this.period}")
            this.scheduledExecutor.scheduleAtFixedRate(
                this::broadcast,
                0,
                this.period,
                TimeUnit.MILLISECONDS
            )
            this.isActive = true
            true
        }
    }

    @Synchronized
    fun stop() {
        if (this.isActive) {
            Log.d(TAG, "stop: ")
            this.scheduledExecutor.shutdown()
        } else {
            Log.d(TAG, "stop: could not stop is inactive")
        }

    }

    @Synchronized
    private fun broadcast() {
        val id = this.idGenerator.next()
        val advertisementPackage = AdvertisementPackage(id)
        if(id.code % 2 == 0) {
            this.addMessages(advertisementPackage)
        }else {
            this.addNeighbours(advertisementPackage)
        }
        if (advertisementPackage.size > 2) {
            if (this.broadCaster.send(advertisementPackage.toString())) {
                Log.i(TAG, "broadcast: $advertisementPackage")
            } else {
                Log.w(TAG, "broadcast failed")
                this.messageQueue.add(advertisementPackage.toString())
            }
        }
    }


    private fun addMessages(advertisementPackage: AdvertisementPackage) {
        while (advertisementPackage.size < this.sizeLimit && this.messageQueue.isNotEmpty()) {
            val msg = this.messageQueue.removeFirst()
            if (msg.length + advertisementPackage.size > this.sizeLimit) {
                this.messageQueue.add(0, msg.substring(this.sizeLimit - advertisementPackage.size))
                advertisementPackage.addCutMessageEnd(
                    msg.substring(
                        0,
                        this.sizeLimit - advertisementPackage.size
                    )
                )
            } else {
                if (!msg.contains('{')) {
                    advertisementPackage.addCutMessageBegin(msg)
                } else if (!msg.contains('}')) {
                    advertisementPackage.addCutMessageEnd(msg)
                } else {
                    val advertisement = Advertisement.Builder().rawMessage(msg).build()
                    advertisementPackage.addAdvertisement(advertisement)
                }
            }
        }
        Log.d(TAG, "addMessages() total ${advertisementPackage.getMessageList().size} messages")
    }

    private fun addNeighbours(advertisementPackage: AdvertisementPackage) {
        var advertisementCounter = 0
        var advertisement = this.advertisementQueue.getNextElement()?.advertisement ?: return
        while ((advertisementPackage.size + advertisement.toString().length) < this.sizeLimit && advertisementCounter < this.advertisementQueue.getSize()) {
            advertisement = this.advertisementQueue.getNextElement()?.advertisement ?: return
            advertisementPackage.addAdvertisement(advertisement)
            advertisementCounter++
        }
        Log.d(TAG, "addNeighbours() total $advertisementCounter neighbour advertisements")
    }

    @Synchronized
    fun addToQueue(message: String) {
        Log.d(TAG, "addToQueue() called with: message = $message")
        this.messageQueue.add(message)
    }
}