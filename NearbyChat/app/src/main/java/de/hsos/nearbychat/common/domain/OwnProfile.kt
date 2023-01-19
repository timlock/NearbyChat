package de.hsos.nearbychat.common.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class OwnProfile(@PrimaryKey var address: String = "") {
    var name: String = ""
    var description: String = ""
    var color: Int = 0
}