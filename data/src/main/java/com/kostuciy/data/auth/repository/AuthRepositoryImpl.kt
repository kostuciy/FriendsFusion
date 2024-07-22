package com.kostuciy.data.auth.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.kostuciy.data.core.db.dao.AuthDao
import com.kostuciy.data.core.db.entity.UserEntity
import com.kostuciy.data.core.utils.FlowUtils.asResult
import com.kostuciy.data.core.utils.ModelUtils.toEntity
import com.kostuciy.domain.auth.model.MessengerUser
import com.kostuciy.domain.auth.model.Token
import com.kostuciy.domain.auth.model.User
import com.kostuciy.domain.auth.model.UserProfile
import com.kostuciy.domain.auth.repository.AuthRepository
import com.kostuciy.domain.core.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

const val VK_ID = "vk_user_id"
const val VK_TOKEN = "vk_token"
const val VK_COLLECTION_USERS = "users"

// TODO: check if Success(false) is needed

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
        private val firebaseFirestore: FirebaseFirestore,
        private val dao: AuthDao,
    ) : AuthRepository {
        override val authData: Flow<User?> =
            dao.getCurrentUserFlow().zip(
                dao.getTokensFlow(),
            ) { userWithMessengers, tokenEntities ->
                userWithMessengers?.let {
                    val tokens = tokenEntities.map { it.toModel() }
                    val profile = UserProfile(it.user.email!!, tokens)
                    val linkedMessengerUsers =
                        it.linkedMessengerUsers
                            .map { it.toModel() }

                    User(it.user.id, it.user.username, it.user.avatarUrl, linkedMessengerUsers, profile)
                }
            }

        //    adds user and its profile data to bd
        override suspend fun updateAuthData(): Flow<Result<Boolean>> =
            flow {
                val user = firebaseAuth.currentUser

                if (user == null) {
                    signOut()
                    emit(false)
                    return@flow
                }

//            tokens get inserted from shared prefs each time
                val tokens = getTokens()
                dao.insertTokens(tokens.map { it.toEntity() })

//            user and user profile are readded to db only when firebase and local user
//            are not equal
                val localUser = dao.getCurrentUser()?.user
                if (compareLocalAndFirebaseUser(user, localUser)) {
                    emit(true)
                    return@flow
                }
//                    return Result.Success(true)

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

        //    adds user data to bd (does not include linked messengers and tokens)
        override suspend fun signUp(
            email: String,
            password: String,
            username: String,
        ): Flow<Result<Boolean>> =
            flow {
                val user =
                    firebaseAuth
                        .createUserWithEmailAndPassword(email, password)
                        .await()
                        .user!!
                user
                    .updateProfile(
                        userProfileChangeRequest {
                            displayName = username
                        },
                    ).await()

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

//    TODO: change to check if has cached data
        override suspend fun signIn(
            email: String,
            password: String,
        ): Flow<Result<Boolean>> =
            flow {
                val user =
                    firebaseAuth
                        .signInWithEmailAndPassword(email, password)
                        .await()
                        .user
                if (user == null) {
                    emit(true)
                    return@flow
                }

                val linkedUsers = getMessengerUsers()
                val tokens = getTokens()
                val profile = UserProfile(user.email!!, tokens)

                dao.insertUser(
                    UserEntity(
                        user.uid,
                        user.displayName!!,
                        user.photoUrl?.toString(),
                        profile.email,
                        true,
                    ),
                )
                dao.insertTokens(tokens.map { it.toEntity() })
                dao.insertMessengers(linkedUsers.map { it.toEntity(user.uid) })

                emit(true)
            }.asResult()

        override suspend fun signOut(): Flow<Result<Boolean>> =
            flow {
                firebaseAuth.signOut()
                dao.clearUser()
                dao.clearTokens()
                dao.clearMessengers()
                emit(true)
            }.asResult()

//    TODO: redo email editing
        override suspend fun editUser(
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

//    TODO: redo tokens in general
        override suspend fun saveVkToken(vkToken: Token.VKToken?): Flow<Result<Boolean>> =
            flow {
                dao.insertTokens(
                    listOf(vkToken?.toEntity() ?: throw IllegalAccessError("No current user")),
                )
                emit(true)

//            val vkTokenMap = mapOf(
//                VK_ID to vkToken.id,
//                VK_TOKEN to vkToken.accessToken
//            )
//
//            firebaseAuth.currentUser?.let {
//                firebaseFirestore.collection(VK_COLLECTION_USERS)
//                    .document(it.uid)
//                    .set(vkTokenMap)
//                    .await()
//                Response.Success(
//                    User(it.uid, it.email!!, it.displayName!!, vkToken)
//                )
//            } ?: throw IllegalAccessError("No current user") //*************************************
            }.asResult()

//    TODO: redo or delete
        private suspend fun getVkToken(uid: String): Token.VKToken? {
            val document =
                firebaseFirestore
                    .collection(VK_COLLECTION_USERS)
                    .document(uid)
                    .get()
                    .await()

            return document.data?.let { vkTokenMap ->
                val vkId = vkTokenMap[VK_ID] as? Long ?: return@let null
                val vkToken = vkTokenMap[VK_TOKEN] as? String ?: return@let null

                Token.VKToken(vkId, vkToken)
            }
        }

        private fun getTokens(): List<Token> {
//        TODO: do with shared prefs
            return emptyList()
        }

        private suspend fun getMessengerUsers(): List<MessengerUser> {
//        TODO: figure out if needs to be executed there
            return emptyList()
        }

        private fun compareLocalAndFirebaseUser(
            firebaseUser: FirebaseUser,
            localUser: UserEntity?,
        ) = localUser?.id == firebaseUser.uid
    }
