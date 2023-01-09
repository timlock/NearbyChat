package de.hsos.nearbychat.app.application

import de.hsos.nearbychat.app.data.Database
import de.hsos.nearbychat.app.data.Repository

class Application: android.app.Application() {
    private val database by lazy { Database.getDatabase(this) }
    val repository by lazy { Repository(database) }

}