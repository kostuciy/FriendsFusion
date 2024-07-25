package com.kostuciy.data.profile.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kostuciy.domain.profile.model.MessengerUser

@Entity(tableName = "users_messengers")
data class MessengerUserEntity(
    @PrimaryKey val id: Long,
    val userId: String? = null,
    val name: String,
    val type: MessengerType,
    val avatarUrl: String? = null,
) {
    fun toModel(): MessengerUser =
        when (this.type) {
            MessengerType.VK -> MessengerUser.VKUser(id, name, avatarUrl)
            MessengerType.TELEGRAM -> MessengerUser.TelegramUser(id, name)
            // TODO: change telegram
        }
}
