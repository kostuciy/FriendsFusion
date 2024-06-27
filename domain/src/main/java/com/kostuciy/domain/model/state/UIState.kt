package com.kostuciy.domain.model.state

sealed class UIState {

    data object Loading : UIState()
    data object Showing : UIState()
    data class Error(val e: Exception) : UIState()
}