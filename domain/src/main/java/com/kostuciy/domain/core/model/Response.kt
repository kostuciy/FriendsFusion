package com.kostuciy.domain.core.model

sealed class Response<out T> {

    data class Success<out T>(
        val data: T
    ): Response<T>()

    data class Failure(
        val exception: Exception
    ): Response<Nothing>()
}