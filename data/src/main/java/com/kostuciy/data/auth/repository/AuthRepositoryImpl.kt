package com.kostuciy.data.auth.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.kostuciy.data.auth.dao.AuthDao
import com.kostuciy.data.core.utils.FlowUtils.asResult
import com.kostuciy.data.profile.entity.UserEntity
import com.kostuciy.domain.auth.repository.AuthRepository
import com.kostuciy.domain.core.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
        private val dao: AuthDao,
    ) : AuthRepository {
        //    adds user and its profile data to bd
        override suspend fun updateAuthData(): Flow<Result<Boolean>> =
            flow {
                val user = firebaseAuth.currentUser
                if (user == null) {
                    signOut()
                    emit(false)
                    return@flow
                }
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
                    signOut()
                    emit(false)
                    return@flow
                }

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

        override suspend fun signOut(): Flow<Result<Boolean>> =
            flow {
                firebaseAuth.signOut()
                dao.clearUser()
                dao.clearTokens()
                dao.clearMessengers()
                emit(false)
            }.asResult()
    }
