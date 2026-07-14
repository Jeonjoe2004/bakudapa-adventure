package com.bakudapa.adventure.core.data

/**
 * A generic class that holds a value with its loading status.
 * Used primarily in the Domain and Data layers to handle operations.
 */
sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val exception: Throwable) : DataResult<Nothing>()
    object Loading : DataResult<Nothing>()
}
