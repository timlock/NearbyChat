package de.hsos.nearbychat.app.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hsos.nearbychat.app.domain.Message

@Dao
interface MessageDao {
    @Query("SELECT * FROM Message WHERE macAddress = :macAddress ORDER BY timeStamp ASC")
    fun get(macAddress: String): LiveData<List<Message>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: Message)

    @Query("DELETE FROM Message WHERE macAddress = :macAddress")
    suspend fun delete(macAddress: String)
}