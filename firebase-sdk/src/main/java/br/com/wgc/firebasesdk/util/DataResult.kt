package br.com.wgc.firebasesdk.util

sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Failure(val error: AppError) : DataResult<Nothing>()
}