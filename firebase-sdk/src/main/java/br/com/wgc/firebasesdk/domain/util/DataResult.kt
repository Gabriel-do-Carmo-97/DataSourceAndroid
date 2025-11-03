package br.com.wgc.firebasesdk.domain.util

sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Failure(val error: AppError) : DataResult<Nothing>()
    data object Loading : DataResult<Nothing>()
}