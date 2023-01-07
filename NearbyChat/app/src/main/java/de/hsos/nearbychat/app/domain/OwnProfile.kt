package de.hsos.nearbychat.app.domain

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.roundToInt

@Entity
class OwnProfile() {
    @PrimaryKey
    var id: Int = 0
    var name: String = ""
    var description: String = ""
    var color: Int = 0
}