package de.hsos.nearbychat.common.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import de.hsos.nearbychat.common.domain.Message
import de.hsos.nearbychat.common.domain.OwnProfile
import de.hsos.nearbychat.common.domain.Profile

@androidx.room.Database(entities = [Message::class, Profile::class, OwnProfile::class], version = 1, exportSchema = false)
abstract class Database: RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun profileDao(): ProfileDao
    abstract fun onwProfileDao(): OwnProfileDao

    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        fun getDatabase(context: Context): Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Database::class.java,
                    "nearby_chat_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}