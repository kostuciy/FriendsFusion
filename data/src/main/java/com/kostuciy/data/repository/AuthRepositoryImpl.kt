package com.kostuciy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.kostuciy.domain.model.Response
import com.kostuciy.domain.model.User
import com.kostuciy.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override suspend fun getAuthData(): Response<User?> =
        try {
            firebaseAuth.currentUser?.let {
                Response.Success(User(it.uid, it.email!!, it.displayName!!))
            } ?: Response.Success(null)
        } catch (e: Exception) {
            Response.Failure(e)
        }

    override suspend fun signUp(email: String, password: String, username: String): Response<User> =
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

                    User(it.uid, it.email!!, it.displayName!!)
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
                    User(it.uid, it.email!!, it.displayName!!)
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

//    TODO: edit email and password
    override suspend fun editUser(
        email: String,
        password: String,
        username: String
    ): Response<User> =
        try {
            val user = firebaseAuth.currentUser!!.let {
                it.updateProfile(
                    userProfileChangeRequest {
                        displayName = username
                    }
                ).await()

                User(it.uid, it.email!!, it.displayName!!)
            }
            Response.Success(user)
        } catch (e: Exception) {
            Response.Failure(e)
        }
}