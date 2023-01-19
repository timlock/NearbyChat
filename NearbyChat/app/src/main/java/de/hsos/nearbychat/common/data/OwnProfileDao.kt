package de.hsos.nearbychat.common.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hsos.nearbychat.common.domain.OwnProfile

@Dao
interface OwnProfileDao {
    @Query("SELECT * FROM OwnProfile")
    fun get(): LiveData<OwnProfile?>

    @Query("SELECT * FROM OwnProfile")
    fun getRaw(): OwnProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(ownProfile: OwnProfile)
}