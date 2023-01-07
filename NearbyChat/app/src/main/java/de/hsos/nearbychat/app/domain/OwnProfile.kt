package de.hsos.nearbychat.app.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * adresse kann so erstellt werden:
 * val androidID : String = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
 */
@Entity
class OwnProfile {
    @PrimaryKey
    var address: String = ""
    var name: String = ""
    var description: String = ""
    var color: Int = 0
}