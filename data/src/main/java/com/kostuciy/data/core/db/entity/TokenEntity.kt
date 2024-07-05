package com.kostuciy.data.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kostuciy.domain.auth.model.Token

@Entity(tableName = "tokens")
data class TokenEntity(
    @PrimaryKey val id: Long,
    val accessToken: String,
    val messenger: MessengerType
) {

    fun toModel(): Token = when (this.messenger) {
        MessengerType.VK -> Token.VKToken(id, accessToken)
        MessengerType.TELEGRAM -> Token.VKToken(id, accessToken) // TODO: change to telegram
    }
}

// TODO: decide how to store
enum class MessengerType {
    VK, TELEGRAM
}

