package de.hsos.nearbychat.app.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hsos.nearbychat.app.domain.Message

@Dao
interface MessageDao {
    @Query("SELECT * FROM Message WHERE address = :address ORDER BY timeStamp ASC")
    fun get(address: String): LiveData<List<Message>>

    @Query("SELECT * FROM Message WHERE address = :address AND timeStamp = :timestamp AND isSelfAuthored = :isSelfAuthored")
    fun get(address: String, timestamp: Long, isSelfAuthored: Boolean): List<Message>

    @Query("SELECT * FROM Message WHERE isSelfAuthored = 1 AND isReceived = 0")
    fun getUnsentMessages(): List<Message>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(message: Message)

    @Query("DELETE FROM Message WHERE address = :address")
    suspend fun delete(address: String)
}