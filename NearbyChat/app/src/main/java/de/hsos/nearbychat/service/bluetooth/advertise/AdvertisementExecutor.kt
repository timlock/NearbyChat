package de.hsos.nearbychat.service.bluetooth.advertise

import android.util.Log
import de.hsos.nearbychat.service.bluetooth.Advertiser
import de.hsos.nearbychat.service.bluetooth.util.Advertisement
import de.hsos.nearbychat.service.bluetooth.util.AdvertisementPackage
import de.hsos.nearbychat.service.bluetooth.util.AtomicIdGenerator
import de.hsos.nearbychat.service.bluetooth.util.Neighbour
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class AdvertisementExecutor(
    private val broadCaster: Advertiser,
    val period: Long,
    private val sizeLimit: Int,
    private val neighbourQueue: AdvertisementQueue<Neighbour>
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
        if (id.code % 2 == 0) {
            this.addMessages(advertisementPackage)
        } else {
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
        while (this.messageQueue.isNotEmpty()) {
            var message = this.messageQueue.removeFirst()
            if(advertisementPackage.size + message.length < this.sizeLimit){
                if (!message.contains('{')) {
                    advertisementPackage.addCutMessageBegin(message)
                }else if (!message.contains('}')) {
                    advertisementPackage.addCutMessageEnd(message)
                }else {
                    advertisementPackage.addAdvertisement(
                        Advertisement.Builder().rawMessage(message).build()
                    )
                }
            }else{
                if(message.length > this.sizeLimit){
                    advertisementPackage.addCutMessageEnd(message.substring(0, this.sizeLimit - advertisementPackage.size))
                    this.messageQueue.add(0,message.substring(this.sizeLimit - advertisementPackage.size - 1))
                }else{
                    this.messageQueue.add(0,message)
                }
                return
            }
        }
    }


    private fun addNeighbours(advertisementPackage: AdvertisementPackage) {
        var advertisementCounter = 0
        while (advertisementCounter < this.neighbourQueue.getSize()){
            var advertisement = this.neighbourQueue.getNextElement()?.advertisement ?: return
            advertisementCounter++
            if((advertisementPackage.size + advertisement.toString().length) > this.sizeLimit){
                return
            }
            advertisementPackage.addAdvertisement(advertisement)

        }
        Log.d(TAG, "addNeighbours() total $advertisementCounter neighbour advertisements")
    }

    @Synchronized
    fun addToQueue(message: String) {
        Log.d(TAG, "addToQueue() called with: message = $message")
        this.messageQueue.add(message)
    }
}