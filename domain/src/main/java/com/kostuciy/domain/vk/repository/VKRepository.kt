package com.kostuciy.domain.vk.repository

interface VKRepository {

    suspend fun getProfileInfo()
}