package com.kostuciy.data.auth.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.kostuciy.domain.auth.model.Response
import com.kostuciy.domain.auth.model.User
import com.kostuciy.domain.auth.repository.AuthRepository
import com.kostuciy.domain.vk.model.VKUserToken
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

const val VK_ID = "vk_user_id"
const val VK_TOKEN = "vk_token"
const val VK_USER_LOGIN = "vk_user_login"
const val VK_COLLECTION_USERS = "users"

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : AuthRepository {
    override suspend fun getAuthData(): Response<User?> =
        try {
            firebaseAuth.currentUser?.let {
                Response.Success(User(it.uid, it.email!!, it.displayName!!, getVkToken(it.uid)))
            } ?: Response.Success(null)
        } catch (e: Exception) {
            Response.Failure(e)
        }

    override suspend fun signUp(
        email: String,
        password: String,
        username: String
    ): Response<User> =
        try {
            val user = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()
                .user!!.let {
                    it.updateProfile(
                        userProfileChangeRequest {
                            displayName = username
                        }
                    ).await()

                    User(it.uid, it.email!!, it.displayName!!, getVkToken(it.uid))
                }
            Response.Success(user)
        } catch (e: Exception) {
            Response.Failure(e)
        }

    override suspend fun signIn(email: String, password: String): Response<User?> =
        try {
            val user = firebaseAuth.signInWithEmailAndPassword(email, password)
                .await()
                .user?.let {
                    User(it.uid, it.email!!, it.displayName!!, getVkToken(it.uid))
                }
            Response.Success(user)
        } catch (e: Exception) {
            Response.Failure(e)
        }

    override suspend fun signOut(): Response<Boolean> =
        try {
            firebaseAuth.signOut()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }

//    TODO: redo email editing
    override suspend fun editUser(
        email: String,
        password: String,
        username: String
    ): Response<User> =
        try {
            val user = firebaseAuth.currentUser!!.let {
                if (it.displayName != username) it.updateProfile(
                    userProfileChangeRequest { displayName = username }
                ).await()

                if (it.email != email) it.updateEmail(email).await()

                if (password.isNotBlank()) it.updatePassword(password).await()
                User(it.uid, it.email!!, it.displayName!!, getVkToken(it.uid))
            }
            Response.Success(user)
        } catch (e: Exception) {
            Response.Failure(e)
        }

    override suspend fun saveVkTokenToFirebase(vkUserToken: VKUserToken): Response<User> =
        try {
            val vkTokenMap = mapOf(
                VK_ID to vkUserToken.id,
                VK_TOKEN to vkUserToken.accessToken,
                VK_USER_LOGIN to vkUserToken.login
            )

            firebaseAuth.currentUser?.let {
                firebaseFirestore.collection(VK_COLLECTION_USERS)
                    .document(it.uid)
                    .set(vkTokenMap)
                    .await()
                Response.Success(User(it.uid, it.email!!, it.displayName!!, vkUserToken))
            } ?: throw IllegalAccessError("No current user")

        } catch (e: Exception) {
            Response.Failure(e)
        }

    private suspend fun getVkToken(uid: String) : VKUserToken? {
        val document = firebaseFirestore
            .collection(VK_COLLECTION_USERS)
            .document(uid).get()
            .await()

        return document.data?.let { vkTokenMap ->
            val vkId = vkTokenMap[VK_ID] as? Long ?: return@let null
            val vkToken = vkTokenMap[VK_TOKEN] as? String ?: return@let null
            val login = vkTokenMap[VK_USER_LOGIN] as String?

            VKUserToken(vkId, vkToken, login)
        }
    }

}