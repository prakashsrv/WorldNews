package com.example.worldnews.ui.base

//coz we want fixed set of states
//“Sealed class helps model mutually exclusive UI states in a type-safe way.”
sealed class UiState<out T>{

    // only these states are allowed

    data class Success<out T>(val data:T) : UiState<T>()

    data class Error(val message:String?=null): UiState<Nothing>()

    object Loading: UiState<Nothing>()

    object Initial : UiState<Nothing>()



}