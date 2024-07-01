package com.kostuciy.friendsfusion.di

import com.kostuciy.data.repository.AuthRepositoryImpl
import com.kostuciy.domain.repository.AuthRepository
import com.kostuciy.domain.usecase.EditUserUseCase
import com.kostuciy.domain.usecase.GetAuthStateUseCase
import com.kostuciy.domain.usecase.RegisterUseCase
import com.kostuciy.domain.usecase.SignInUseCase
import com.kostuciy.domain.usecase.SignOutUseCase
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
    fun provideGetAuthUseCase(repository: AuthRepository) =
        GetAuthStateUseCase(repository)

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
}
