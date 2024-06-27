package com.kostuciy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.kostuciy.domain.model.Response
import com.kostuciy.domain.model.User
import com.kostuciy.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override suspend fun getAuthData(): Response<User?> =
        try {
            firebaseAuth.currentUser?.let {
                Response.Success(User(it.uid, it.email))
            } ?: Response.Success(null)
        } catch (e: Exception) {
            Response.Failure(e)
        }

    override suspend fun register(email: String, password: String): Response<Boolean> =
        try {
            Response.Success(
                firebaseAuth.createUserWithEmailAndPassword(email, password).result != null
            )
        } catch (e: Exception) {
            Response.Failure(e)
        }

    override suspend fun signIn(email: String, password: String): Response<Boolean> =
        try {
            Response.Success(
                firebaseAuth.signInWithEmailAndPassword(email, password).result != null
            )
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

}