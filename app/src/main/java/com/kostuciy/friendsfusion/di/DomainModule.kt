package com.kostuciy.friendsfusion.di

import com.kostuciy.data.auth.repository.AuthRepositoryImpl
import com.kostuciy.domain.auth.repository.AuthRepository
import com.kostuciy.domain.auth.usecase.EditUserUseCase
import com.kostuciy.domain.auth.usecase.GetAuthDataUseCase
import com.kostuciy.domain.auth.usecase.UpdateAuthDataUseCase
import com.kostuciy.domain.auth.usecase.RegisterUseCase
import com.kostuciy.domain.auth.usecase.SaveVKTokenToFirestoreUseCase
import com.kostuciy.domain.auth.usecase.SignInUseCase
import com.kostuciy.domain.auth.usecase.SignOutUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

}

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideGetAuthDataUseCase(repository: AuthRepository) =
        GetAuthDataUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideUpdateAuthDataUseCase(repository: AuthRepository) =
        UpdateAuthDataUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideRegisterUseCase(repository: AuthRepository) =
        RegisterUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideSignInUseCase(repository: AuthRepository) =
        SignInUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideSignOutUseCase(repository: AuthRepository) =
        SignOutUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideEditUserUseCase(repository: AuthRepository) =
        EditUserUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideSaveVKTokenUseCase(repository: AuthRepository) =
        SaveVKTokenToFirestoreUseCase(repository)
}
