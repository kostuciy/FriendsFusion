package com.kostuciy.data.core.utils

import com.kostuciy.domain.core.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

object FlowUtils {
    fun <T> Flow<T>.asResult(): Flow<Result<T>> =
        this
            .map<T, Result<T>> { Result.Success(it) }
            .onStart { emit(Result.Loading) }
            .catch { emit(Result.Error(it)) }
}
