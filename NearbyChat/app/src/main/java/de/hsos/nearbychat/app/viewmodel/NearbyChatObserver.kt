package de.hsos.nearbychat.app.viewmodel

import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.Profile

interface NearbyChatObserver {
    fun onBound()
    fun onProfile(profile: Profile)
    fun onMessage(message: Message)
}
