package de.hsos.nearbychat.app.viewmodel

import de.hsos.nearbychat.common.domain.Profile

interface NearbyChatObserver {
    fun onBound()
    fun onProfile(profile: Profile)
    fun onProfileTimeout(address: String)
}
