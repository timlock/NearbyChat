package de.hsos.nearbychat.app.domain

data class Profile(val macAddress: String) {
    private lateinit var name: String
    private lateinit var description: String
    private var isAvailable: Boolean = false
    private var neighbors: List<Profile> = mutableListOf<Profile>()
    private var rssi: Int = 0
}