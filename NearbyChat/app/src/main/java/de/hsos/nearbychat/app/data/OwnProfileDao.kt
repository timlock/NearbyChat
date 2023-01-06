package de.hsos.nearbychat.app.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hsos.nearbychat.app.domain.OwnProfile

@Dao
interface OwnProfileDao {
    @Query("SELECT * FROM OwnProfile")
    fun get(): LiveData<OwnProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ownProfile: OwnProfile)
}