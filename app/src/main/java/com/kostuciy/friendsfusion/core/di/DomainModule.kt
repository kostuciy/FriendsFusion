package com.kostuciy.friendsfusion.core.di

import com.kostuciy.data.auth.repository.AuthRepositoryImpl
import com.kostuciy.data.profile.repository.ProfileRepositoryImpl
import com.kostuciy.domain.auth.repository.AuthRepository
import com.kostuciy.domain.auth.usecase.RegisterUseCase
import com.kostuciy.domain.auth.usecase.SignInUseCase
import com.kostuciy.domain.auth.usecase.SignOutUseCase
import com.kostuciy.domain.auth.usecase.UpdateAuthDataUseCase
import com.kostuciy.domain.profile.repository.ProfileRepository
import com.kostuciy.domain.profile.usecase.EditProfileUseCase
import com.kostuciy.domain.profile.usecase.GetProfileDataUseCase
import com.kostuciy.domain.profile.usecase.SaveTokenUseCase
import com.kostuciy.domain.profile.usecase.UpdateProfileDataUseCase
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
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository
}

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideUpdateAuthDataUseCase(repository: AuthRepository) = UpdateAuthDataUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideRegisterUseCase(repository: AuthRepository) = RegisterUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideSignInUseCase(repository: AuthRepository) = SignInUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideSignOutUseCase(repository: AuthRepository) = SignOutUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideEditProfileUserUseCase(repository: ProfileRepository) = EditProfileUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetProfileDataUseCase(repository: ProfileRepository) = GetProfileDataUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideSaveTokenUseCase(repository: ProfileRepository) = SaveTokenUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideUpdateProfileDataUseCase(repository: ProfileRepository) = UpdateProfileDataUseCase(repository)
}
