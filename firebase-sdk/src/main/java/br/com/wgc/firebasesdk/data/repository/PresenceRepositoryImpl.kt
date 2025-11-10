package br.com.wgc.firebasesdk.data.repository

import br.com.wgc.firebasesdk.data.model.database.presence.PresenceState
import br.com.wgc.firebasesdk.domain.repository.PresenceRepository
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
import java.io.IOException

internal class PresenceRepositoryImpl(
    private val database: FirebaseDatabase,
) : PresenceRepository {
    private val presenceRef = database.getReference("presence")

    override suspend fun goOnline(entityType: String, entityId: String): DataResult<Unit> = runCatching {
        val entityPresenceRef = presenceRef.child(entityType).child(entityId)

        database.getReference(".info/connected").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue(Boolean::class.java) == true) {
                    entityPresenceRef.setValue(PresenceState(isOnline = true))
                    entityPresenceRef.onDisconnect().setValue(PresenceState(isOnline = false))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Não é comum um erro aqui, mas logar seria uma boa prática.
            }
        })
        DataResult.Success(Unit)
    }.getOrElse {
        DataResult.Failure(mapExceptionToAppError(it))
    }

    override suspend fun goOffline(entityType: String, entityId: String): DataResult<Unit> = runCatching {
        val entityPresenceRef = presenceRef.child(entityType).child(entityId)
        // Remove o gatilho onDisconnect antes de ficar offline manualmente.
        entityPresenceRef.onDisconnect().cancel()
        entityPresenceRef.setValue(PresenceState(isOnline = false)).await()
        DataResult.Success(Unit)
    }.getOrElse {
        DataResult.Failure(mapExceptionToAppError(it))
    }

    override fun trackPresence(entityType: String, entityId: String): Flow<DataResult<PresenceState>> = callbackFlow {
        val entityPresenceRef = presenceRef.child(entityType).child(entityId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue<PresenceState>()?.let {
                    trySend(DataResult.Success(it))
                } ?: trySend(DataResult.Success(PresenceState(isOnline = false))) // Se não houver nó, assume offline
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(DataResult.Failure(mapDatabaseErrorToAppError(error)))
                close(error.toException())
            }
        }
        entityPresenceRef.addValueEventListener(listener)
        awaitClose { entityPresenceRef.removeEventListener(listener) }
    }
    
    private fun mapExceptionToAppError(exception: Throwable): AppError {
        return when (exception) {
            is DatabaseException -> {
                when {
                    exception.message?.contains("permission_denied", ignoreCase = true) == true ->
                        AppError.Database.PERMISSION_DENIED
                    else -> AppError.Database.OPERATION_FAILED
                }
            }
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
    }

    private fun mapDatabaseErrorToAppError(error: DatabaseError): AppError {
        return when (error.code) {
            DatabaseError.PERMISSION_DENIED -> AppError.Database.PERMISSION_DENIED
            else -> AppError.Generic.UNKNOWN_ERROR
        }
    }
}
