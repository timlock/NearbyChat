package de.hsos.nearbychat.app.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hsos.nearbychat.app.domain.Profile

@Dao
interface ProfileDao {
    @Query("SELECT * FROM Profile ORDER BY lastInteraction DESC")
    fun get(): LiveData<List<Profile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: Profile)

    @Query("DELETE FROM Profile WHERE macAddress = :macAddress")
    suspend fun delete(macAddress: String)
}