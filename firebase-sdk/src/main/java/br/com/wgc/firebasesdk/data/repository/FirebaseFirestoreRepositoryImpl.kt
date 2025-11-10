package br.com.wgc.firebasesdk.data.repository

import br.com.wgc.firebasesdk.data.model.firestore.Filter
import br.com.wgc.firebasesdk.data.model.firestore.Operator
import br.com.wgc.firebasesdk.domain.repository.FirebaseFirestoreRepository
import br.com.wgc.firebasesdk.domain.util.AppError
import br.com.wgc.firebasesdk.domain.util.DataResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class FirebaseFirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirebaseFirestoreRepository {

    override suspend fun <T : Any> addDocument(
        collection: String,
        data: T,
        customId: String?
    ): DataResult<String> = runCatching {
        val documentReference = if (customId != null) {
            firestore.collection(collection).document(customId)
        } else {
            firestore.collection(collection).document()
        }
        documentReference.set(data).await()
        DataResult.Success(documentReference.id)
    }.getOrElse {
        DataResult.Failure(mapExceptionToAppError(it))
    }

    override suspend fun <T : Any> getDocument(
        collection: String,
        documentId: String,
        clazz: Class<T>
    ): DataResult<T?> = runCatching {
        val snapshot = firestore.collection(collection).document(documentId).get().await()
        DataResult.Success(snapshot.toObject(clazz))
    }.getOrElse {
        DataResult.Failure(mapExceptionToAppError(it))
    }

    override suspend fun updateDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any>
    ): DataResult<Unit> = runCatching {
        firestore.collection(collection).document(documentId).update(data).await()
        DataResult.Success(Unit)
    }.getOrElse {
        DataResult.Failure(mapExceptionToAppError(it))
    }

    override suspend fun deleteDocument(
        collection: String,
        documentId: String
    ): DataResult<Unit> = runCatching {
        firestore.collection(collection).document(documentId).delete().await()
        DataResult.Success(Unit)
    }.getOrElse {
        DataResult.Failure(mapExceptionToAppError(it))
    }

    override suspend fun <T : Any> findDocuments(
        collection: String,
        filters: List<Filter>,
        clazz: Class<T>
    ): DataResult<List<T>> = runCatching {
        val query = buildQuery(collection, filters)
        val snapshot = query.get().await()
        DataResult.Success(snapshot.toObjects(clazz))
    }.getOrElse {
        DataResult.Failure(mapExceptionToAppError(it))
    }

    override fun <T : Any> listenToDocument(
        collection: String,
        documentId: String,
        clazz: Class<T>
    ): Flow<DataResult<T?>> = callbackFlow {
        val listener = firestore.collection(collection).document(documentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Failure(mapExceptionToAppError(error)))
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(DataResult.Success(snapshot.toObject(clazz)))
                } else {
                    trySend(DataResult.Success(null)) // Documento não existe
                }
            }
        awaitClose { listener.remove() }
    }

    override fun <T : Any> listenToCollection(
        collection: String,
        filters: List<Filter>,
        clazz: Class<T>
    ): Flow<DataResult<List<T>>> = callbackFlow {
        val query = buildQuery(collection, filters)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResult.Failure(mapExceptionToAppError(error)))
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(DataResult.Success(snapshot.toObjects(clazz)))
            }
        }
        awaitClose { listener.remove() }
    }

    private fun buildQuery(collection: String, filters: List<Filter>): Query {
        var query: Query = firestore.collection(collection)
        filters.forEach { filter ->
            query = when (filter.operator) {
                Operator.EQUAL_TO -> query.whereEqualTo(filter.field, filter.value)
                Operator.NOT_EQUAL_TO -> query.whereNotEqualTo(filter.field, filter.value)
                Operator.GREATER_THAN -> query.whereGreaterThan(filter.field, filter.value)
                Operator.LESS_THAN -> query.whereLessThan(filter.field, filter.value)
                Operator.GREATER_THAN_OR_EQUAL_TO -> query.whereGreaterThanOrEqualTo(filter.field, filter.value)
                Operator.LESS_THAN_OR_EQUAL_TO -> query.whereLessThanOrEqualTo(filter.field, filter.value)
                Operator.ARRAY_CONTAINS -> query.whereArrayContains(filter.field, filter.value)
                Operator.ARRAY_CONTAINS_ANY -> query.whereArrayContainsAny(filter.field, filter.value as List<Any>)
                Operator.IN -> query.whereIn(filter.field, filter.value as List<Any>)
                Operator.NOT_IN -> query.whereNotIn(filter.field, filter.value as List<Any>)
            }
        }
        return query
    }

    private fun mapExceptionToAppError(exception: Throwable): AppError {
        return when (exception) {
            is FirebaseFirestoreException -> when (exception.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> AppError.Firestore.PERMISSION_DENIED
                FirebaseFirestoreException.Code.NOT_FOUND -> AppError.Firestore.DOCUMENT_NOT_FOUND
                FirebaseFirestoreException.Code.ABORTED -> AppError.Firestore.ABORTED
                FirebaseFirestoreException.Code.UNAVAILABLE -> AppError.Generic.NETWORK_ERROR
                else -> AppError.Firestore.TRANSACTION_FAILED
            }
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
    }
}
