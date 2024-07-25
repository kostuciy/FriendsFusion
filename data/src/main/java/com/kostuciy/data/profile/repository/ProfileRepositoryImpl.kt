package com.kostuciy.data.profile.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.kostuciy.data.core.utils.FlowUtils.asResult
import com.kostuciy.data.core.utils.ModelUtils.toEntity
import com.kostuciy.data.profile.dao.ProfileDao
import com.kostuciy.data.profile.entity.MessengerType
import com.kostuciy.data.profile.entity.MessengerUserEntity
import com.kostuciy.data.profile.entity.TokenEntity
import com.kostuciy.data.profile.entity.UserEntity
import com.kostuciy.domain.core.model.Result
import com.kostuciy.domain.profile.model.MessengerUser
import com.kostuciy.domain.profile.model.Token
import com.kostuciy.domain.profile.model.User
import com.kostuciy.domain.profile.model.UserProfile
import com.kostuciy.domain.profile.repository.ProfileRepository
import com.vk.api.sdk.VK
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.users.UsersService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore, // TODO: use to save messengers data
    private val dao: ProfileDao,
) : ProfileRepository {
    override val profileData: Flow<User?> =
        dao.getCurrentUserFlow().zip(
            dao.getTokensFlow(),
        ) { userWithMessengers, tokenEntities ->
            userWithMessengers?.let {
                val tokens = tokenEntities.map(TokenEntity::toModel)
                val profile = UserProfile(it.user.email!!, tokens)
                val linkedMessengerUsers =
                    it.linkedMessengerUsers
                        .map(MessengerUserEntity::toModel)

                User(it.user.id, it.user.username, it.user.avatarUrl, linkedMessengerUsers, profile)
            }
        }

    override suspend fun updateProfileData(): Flow<Result<Boolean>> =
        flow {
            val user = firebaseAuth.currentUser

            if (user == null) {
                emit(false)
                return@flow
            }

            val localUser = dao.getCurrentUser()?.user
            if (compareLocalAndFirebaseUser(user, localUser)) {
                emit(true)
                return@flow
            }

            dao.insertUser(
                UserEntity(
                    user.uid,
                    user.displayName!!,
                    user.photoUrl?.toString(),
                    user.email!!,
                    true,
                ),
            )
            emit(true)
        }.asResult()

    //    TODO: redo email editing
    override suspend fun editProfile(
        email: String,
        password: String,
        username: String,
    ): Flow<Result<Boolean>> =
        flow {
            val user = firebaseAuth.currentUser!!

            if (user.displayName != username) {
                user
                    .updateProfile(
                        userProfileChangeRequest { displayName = username },
                    ).await()
            }

            if (user.email != email) user.updateEmail(email).await()

            if (password.isNotBlank()) user.updatePassword(password).await()

            dao.insertUser(
                UserEntity(
                    user.uid,
                    user.displayName!!,
                    user.photoUrl?.toString(),
                    user.email,
                    true,
                ),
            )

            emit(true)
        }.asResult()

    override suspend fun saveToken(token: Token): Flow<Result<Boolean>> =
        flow {
            dao.insertTokens(listOf(token.toEntity()))
            emit(true)
        }.asResult()

    override suspend fun checkVKTokenExists(): Boolean = dao.checkTokenExists(MessengerType.VK)

    override suspend fun checkTelegramTokenExists(): Boolean = dao.checkTokenExists(MessengerType.TELEGRAM)

    private fun compareLocalAndFirebaseUser(
        firebaseUser: FirebaseUser,
        localUser: UserEntity?,
    ) = localUser?.id == firebaseUser.uid
}
