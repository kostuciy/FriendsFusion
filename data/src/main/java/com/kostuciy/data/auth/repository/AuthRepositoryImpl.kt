package com.kostuciy.data.auth.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.kostuciy.data.core.db.dao.AuthDao
import com.kostuciy.data.core.db.entity.UserEntity
import com.kostuciy.data.core.utils.ModelUtils.toEntity
import com.kostuciy.domain.auth.model.Token
import com.kostuciy.domain.core.model.Response
import com.kostuciy.domain.auth.model.User
import com.kostuciy.domain.auth.model.UserProfile
import com.kostuciy.domain.auth.repository.AuthRepository
import com.kostuciy.domain.core.model.MessengerUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

const val VK_ID = "vk_user_id"
const val VK_TOKEN = "vk_token"
const val VK_COLLECTION_USERS = "users"

// TODO: check if Success(false) is needed

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val dao: AuthDao
) : AuthRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val authData: Flow<User?> = dao.getCurrentUser().flatMapMerge { userWithMessengers ->
        dao.getTokens().map { tokenEntities ->
            userWithMessengers?.let {
                val tokens = tokenEntities.map { it.toModel() }
                val profile = UserProfile(it.user.email!!, tokens)
                val linkedMessengerUsers = it.linkedMessengerUsers
                    .map { it.toModel() }

                User(it.user.id, it.user.username, it.user.avatarUrl, linkedMessengerUsers, profile)
            }
        }
    }


    //    adds user and its profile data to bd
    override suspend fun updateAuthData(): Response<Boolean> {
        try {
            val user = firebaseAuth.currentUser ?: return Response.Success(false)

            val linkedUsers = getMessengerUsers()
            val tokens = getTokens()
            val profile = UserProfile(user.email!!, tokens)

            dao.insertUser(
                UserEntity(
                    user.uid, user.displayName!!, user.photoUrl?.toString(),
                    profile.email, true
                )
            )
            dao.insertTokens(tokens.map { it.toEntity() })
            dao.insertMessengers(linkedUsers.map { it.toEntity(user.uid) })

            return Response.Success(true)
        } catch (e: Exception) {
            return Response.Failure(e)
        }
    }

    //    adds user data to bd (does not include linked messengers and tokens)
    override suspend fun signUp(
        email: String,
        password: String,
        username: String
    ): Response<Boolean> {
        try {
            val user = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()
                .user!!
            user.updateProfile(
                userProfileChangeRequest {
                    displayName = username
                }
            ).await()

            dao.insertUser(
                UserEntity(
                    user.uid, user.displayName!!, user.photoUrl?.toString(),
                    user.email, true
                )
            )

            return Response.Success(true)
        } catch (e: Exception) {
            return Response.Failure(e)
        }
    }

//    TODO: change to check if has cached data
    override suspend fun signIn(email: String, password: String): Response<Boolean> {
        try {
            val user = firebaseAuth.signInWithEmailAndPassword(email, password)
                .await()
                .user ?: return Response.Success(false)

            val linkedUsers = getMessengerUsers()
            val tokens = getTokens()
            val profile = UserProfile(user.email!!, tokens)

            dao.insertUser(
                UserEntity(
                    user.uid, user.displayName!!, user.photoUrl?.toString(),
                    profile.email, true
                )
            )
            dao.insertTokens(tokens.map { it.toEntity() })
            dao.insertMessengers(linkedUsers.map { it.toEntity(user.uid) })

            return Response.Success(true)
        } catch (e: Exception) {
            return Response.Failure(e)
        }
    }

    override suspend fun signOut(): Response<Boolean> =
        try {
            firebaseAuth.signOut()
            dao.clearUser()
            dao.clearTokens()
            dao.clearMessengers()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }

//    TODO: redo email editing
    override suspend fun editUser(
        email: String,
        password: String,
        username: String
    ): Response<Boolean> {
    try {
        val user = firebaseAuth.currentUser!!

        if (user.displayName != username) user.updateProfile(
            userProfileChangeRequest { displayName = username }
        ).await()

        if (user.email != email) user.updateEmail(email).await()

        if (password.isNotBlank()) user.updatePassword(password).await()

        dao.insertUser(
            UserEntity(
                user.uid, user.displayName!!, user.photoUrl?.toString(),
                user.email, true
            )
        )

        return Response.Success(true)
    } catch (e: Exception) {
        return Response.Failure(e)
    }
}

//    TODO: redo tokens in general
    override suspend fun saveVkToken(vkToken: Token.VKToken): Response<Boolean> {
        try {

            dao.insertTokens(listOf(vkToken.toEntity()))
            return Response.Success(true)

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

        } catch (e: Exception) {
            return Response.Failure(e)
        }
    }

//    TODO: redo or delete
    private suspend fun getVkToken(uid: String) : Token.VKToken? {
        val document = firebaseFirestore
            .collection(VK_COLLECTION_USERS)
            .document(uid).get()
            .await()

        return document.data?.let { vkTokenMap ->
            val vkId = vkTokenMap[VK_ID] as? Long ?: return@let null
            val vkToken = vkTokenMap[VK_TOKEN] as? String ?: return@let null

            Token.VKToken(vkId, vkToken)
        }
    }

    private fun getTokens() : List<Token> {
//        TODO: do with shared prefs
        return emptyList()
    }

    private suspend fun getMessengerUsers() : List<MessengerUser> {
//        TODO: figure out if needs to be executed there
        return emptyList()

    }

}