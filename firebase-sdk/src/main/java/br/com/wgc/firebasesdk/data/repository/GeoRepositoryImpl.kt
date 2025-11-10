package br.com.wgc.firebasesdk.data.repository

import br.com.wgc.firebasesdk.data.model.database.geo.Location
import java.io.IOException
import br.com.wgc.firebasesdk.domain.repository.GeoRepository
import br.com.wgc.firebasesdk.util.AppError
import br.com.wgc.firebasesdk.util.DataResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

internal class GeoRepositoryImpl(
    private val database: FirebaseDatabase,
) : GeoRepository {
    private val locationsRef = database.getReference("locations")

    override suspend fun updateLocation(
        entityType: String,
        entityId: String,
        location: Location
    ): DataResult<String> = runCatching {
        locationsRef.child(entityType)
            .child(entityId)
            .setValue(location)
            .await()
        DataResult.Success("Localizacao atualizada")
    }.getOrElse { exception ->
        val appError = when (exception) {
            is DatabaseException -> {
                when {
                    exception.message?.contains("permission_denied", ignoreCase = true) == true ->
                        AppError.Database.PERMISSION_DENIED

                    exception.message?.contains("disconnected", ignoreCase = true) == true ->
                        AppError.Database.DISCONNECTED

                    exception.message?.contains("expired_token", ignoreCase = true) == true ->
                        AppError.Database.EXPIRED_TOKEN

                    exception.message?.contains("invalid_token", ignoreCase = true) == true ->
                        AppError.Database.INVALID_TOKEN

                    exception.message?.contains("unavailable", ignoreCase = true) == true ->
                        AppError.Database.UNAVAILABLE

                    exception.message?.contains("write_canceled", ignoreCase = true) == true ->
                        AppError.Database.WRITE_CANCELED

                    else -> AppError.Database.OPERATION_FAILED
                }
            }

            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
        DataResult.Failure(appError)
    }

    override fun trackLocation(entityType: String, entityId: String): Flow<DataResult<Location>> =
        callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue<Location>()?.let {
                        trySend(
                            DataResult.Success(it)
                        )
                    } ?: trySend(
                        DataResult.Failure(AppError.Database.OPERATION_FAILED)
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    val appError = when (error.code) {
                        DatabaseError.PERMISSION_DENIED -> AppError.Database.PERMISSION_DENIED
                        DatabaseError.DATA_STALE -> AppError.Database.DATA_STALE
                        DatabaseError.DISCONNECTED -> AppError.Database.DISCONNECTED
                        DatabaseError.EXPIRED_TOKEN -> AppError.Database.EXPIRED_TOKEN
                        DatabaseError.INVALID_TOKEN -> AppError.Database.INVALID_TOKEN
                        DatabaseError.MAX_RETRIES -> AppError.Database.MAX_RETRIES
                        DatabaseError.OVERRIDDEN_BY_SET -> AppError.Database.OVERRIDDEN_BY_SET
                        DatabaseError.UNAVAILABLE -> AppError.Database.UNAVAILABLE
                        DatabaseError.WRITE_CANCELED -> AppError.Database.WRITE_CANCELED
                        DatabaseError.NETWORK_ERROR -> AppError.Generic.NETWORK_ERROR
                        DatabaseError.OPERATION_FAILED -> AppError.Database.OPERATION_FAILED
                        else -> AppError.Generic.UNKNOWN_ERROR
                    }
                    trySend(DataResult.Failure(appError))
                    close(error.toException())
                }
            }

            val entityLocationRef = locationsRef
                .child(entityType)
                .child(entityId)
            entityLocationRef.addValueEventListener(listener)

            awaitClose { entityLocationRef.removeEventListener(listener) }
        }
}
