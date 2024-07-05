package com.kostuciy.data.core.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.kostuciy.domain.auth.model.Token
import com.kostuciy.domain.auth.model.User
import com.kostuciy.domain.auth.model.UserProfile

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val email: String? = null,   // stored in UserProfile
    val isCurrentUser: Boolean = false
)

data class UserWithMessengers(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val linkedMessengerUsers: List<MessengerUserEntity> = emptyList()
) {

    fun toModel(tokens: List<Token>): User {
        val messengerUsers = linkedMessengerUsers.map { it.toModel() }
        val userProfile =
            if (!user.isCurrentUser) null
            else UserProfile(user.email!!, tokens)

        return User(
            user.id, user.username, user.avatarUrl,
            messengerUsers, userProfile
            )
    }
}