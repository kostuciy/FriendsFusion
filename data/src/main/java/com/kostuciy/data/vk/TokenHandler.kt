package com.kostuciy.data.vk

import com.kostuciy.data.profile.dao.ProfileDao
import com.kostuciy.data.profile.entity.MessengerType
import com.vk.api.sdk.VKTokenExpiredHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenHandler @Inject constructor(
    private val dao: ProfileDao,
) : VKTokenExpiredHandler {
    override fun onTokenExpired() {
        CoroutineScope(Dispatchers.Main).launch {
            dao.deleteToken(MessengerType.VK)
        }
    }
}
