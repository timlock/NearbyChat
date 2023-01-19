package de.hsos.nearbychat.service.bluetooth.advertise

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.core.util.forEach
import de.hsos.nearbychat.service.bluetooth.Advertiser
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


class BluetoothAdvertiser(
    private var bluetoothAdapter: BluetoothAdapter,
    private val advertisingInterval: Int
) :
    Advertiser {
    private val TAG: String = BluetoothAdvertiser::class.java.simpleName
    private var advertiseUUID: ParcelUuid =
        ParcelUuid(UUID.fromString("e889813c-5d19-49e2-8bc4-d4596b4f5250"))
    private var advertiser: BluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
    var maxMessageLength: Int = 0
    private var maxTotalLength: Int = 0
    private var currentAdvertisingParameters: AdvertisingSetParameters
    private lateinit var currentAdvertisingData: AdvertiseData
    private lateinit var currentAdvertisingSet: AdvertisingSet
    private var isAdvertising: Boolean = false


    init {
        Log.d(TAG, "init: ")
        this.maxTotalLength = this.bluetoothAdapter.leMaximumAdvertisingDataLength
        Log.d(TAG, "init: leMaximumAdvertisingDataLength: " + this.maxMessageLength)
        this.currentAdvertisingParameters = AdvertisingSetParameters.Builder()
            .setLegacyMode(false)
            .setInterval(this.advertisingInterval)
            .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_HIGH)
            .setPrimaryPhy(BluetoothDevice.PHY_LE_1M)
            .setSecondaryPhy(BluetoothDevice.PHY_LE_2M)
            .build()
        Log.d(TAG, "init: currentAdvertisingParameters: $currentAdvertisingParameters")
        val dummyData: AdvertiseData = AdvertiseData.Builder()
            .addServiceUuid(advertiseUUID)
            .addServiceData(advertiseUUID, "".encodeToByteArray())
            .build()
        this.maxMessageLength = this.maxTotalLength - this.totalBytes(dummyData)
        Log.d(TAG, "init: maximum message length: " + this.maxMessageLength)
    }


    override fun start(): Boolean {
        this.currentAdvertisingData = AdvertiseData.Builder()
            .addServiceUuid(advertiseUUID)
            .addServiceData(advertiseUUID, "".encodeToByteArray())
            .build()
        Log.i(TAG, "startAdvertising: currentAdvertisingData: $currentAdvertisingData)")
        Log.i(
            TAG,
            "startAdvertising: advertisingData size: " + this.totalBytes(this.currentAdvertisingData)
        )
        return try {
            this.advertiser.startAdvertisingSet(
                this.currentAdvertisingParameters,
                this.currentAdvertisingData,
                null,
                null,
                null,
                this.advertisingSetCallback
            )
            true
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.w(TAG, "initAdvertiser: ", illegalArgumentException)
            false
        } catch (securityException: SecurityException) {
            Log.w(TAG, "initAdvertiser: ", securityException)
            false
        }
    }

    override fun stop() {
        try {
            this.advertiser.stopAdvertisingSet(this.advertisingSetCallback)
        } catch (e: SecurityException) {
            Log.w(TAG, "stop: ", e)
        }
    }

    override fun getMaxMessageSize(): Int = this.maxMessageLength

    override fun send(message: String): Boolean {
        Log.d(TAG, "send() called with: message = $message")
//        Handler(Looper.getMainLooper()).post {
            val advertiseData: AdvertiseData = AdvertiseData.Builder()
                .addServiceUuid(advertiseUUID)
                .addServiceData(this.advertiseUUID, message.encodeToByteArray())
                .build()
            val messageSize = this.totalBytes(advertiseData)
            if (messageSize > this.maxMessageLength) {
                Log.w(TAG, "changeAdvertisingData: message: $message is too large, size: $messageSize")
                return false
            } else if (!this::currentAdvertisingSet.isInitialized) {
                Log.w(TAG, "send: currentAdvertisingSet is not initialized")
                return false
            } else {
                this.currentAdvertisingData = advertiseData
                try {
                    Log.d(TAG, "changeAdvertisingData: message: $message")
                    this.currentAdvertisingSet.setAdvertisingData(
                        this.currentAdvertisingData
                    )
                    return true
                } catch (e: SecurityException) {
                    Log.w(TAG, "initAdvertiser: ", e)
                    return false
                }
            }
//        }
//        return true
    }

    private var advertisingSetCallback: AdvertisingSetCallback = object : AdvertisingSetCallback() {
        override fun onAdvertisingSetStarted(
            advertisingSet: AdvertisingSet,
            txPower: Int,
            status: Int
        ) {
            Log.i(
                TAG, "onAdvertisingSetStarted(): txPower:  $txPower  , status: $status "
            )
            currentAdvertisingSet = advertisingSet
            isAdvertising = true
        }

        override fun onAdvertisingSetStopped(advertisingSet: AdvertisingSet) {
            Log.i(TAG, "onAdvertisingSetStopped():")
        }
    }

    /**
     * Quellen
     * BluetoothLeAdvertiser.totalBytes
     * https://android.googlesource.com/platform/frameworks/base/+/android-9.0.0_r3/core/java/android/bluetooth/BluetoothUuid.java
     * https://android.googlesource.com/platform/frameworks/base/+/010bf37/core/java/android/bluetooth/BluetoothUuid.java
     */
    private fun totalBytes(data: AdvertiseData): Int {
        var size: Int = 0
        if (data.serviceUuids != null) {
            var num128BitUuids: Int = 0
            data.serviceUuids.forEach { _ -> ++num128BitUuids }
            if (num128BitUuids != 0) {
                size += 2 + num128BitUuids * 16
            }
        }
        data.serviceData.keys.forEach { uuid ->
            val msb: Long = uuid.uuid.mostSignificantBits
            val lsb: Long = uuid.uuid.leastSignificantBits
            val uuidBytes: ByteArray = ByteArray(16)
            val buf: ByteBuffer = ByteBuffer.wrap(uuidBytes).order(ByteOrder.LITTLE_ENDIAN)
            buf.putLong(8, msb)
            buf.putLong(0, lsb)
            val uuidLen: Int = uuidBytes.size
            size += 2 + uuidLen + ((data.serviceData[uuid])?.size ?: 0)
        }
        data.manufacturerSpecificData.forEach { _, c -> size += 2 + 2 + c.size }
        if (data.includeTxPowerLevel) {
            size += 2 + 1
        }
        if (data.includeDeviceName) {
            try {
                val length: Int = this.bluetoothAdapter.name.length
                if (length >= 0) {
                    size += 2 + length;
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        return size
    }
}