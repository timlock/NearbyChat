package de.hsos.nearbychat.app.domain

data class Profile(val macAddress: String) {
    lateinit var name: String
    lateinit var description: String
    var isAvailable: Boolean = false
    var neighbors: List<Profile> = mutableListOf<Profile>()
    var rssi: Int = 0
}