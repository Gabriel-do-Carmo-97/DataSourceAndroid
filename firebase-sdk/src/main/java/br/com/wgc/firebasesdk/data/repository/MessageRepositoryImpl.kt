package br.com.wgc.firebasesdk.data.repository

import br.com.wgc.firebasesdk.data.model.database.message.Conversation
import br.com.wgc.firebasesdk.data.model.database.message.Message
import br.com.wgc.firebasesdk.data.model.database.message.MessageStatus
import br.com.wgc.firebasesdk.domain.repository.MessageRepository
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

internal class MessageRepositoryImpl(
    private val database: FirebaseDatabase,
) : MessageRepository {
    private val messagesRef = database.getReference("messages")
    private val conversationsRef = database.getReference("conversations")
    private val usersRef = database.getReference("users") // Assumindo um nó 'users' para busca

    override suspend fun sendMessage(conversationId: String, message: Message): DataResult<Unit> = runCatching {
        val messageId = messagesRef.child(conversationId).push().key
            ?: throw IllegalStateException("Não foi possível gerar a chave da mensagem.")

        val messageToSend = message.copy(messageId = messageId, status = MessageStatus.SENT)

        val updates = mapOf(
            "/messages/$conversationId/$messageId" to messageToSend,
            "/conversations/$conversationId/lastMessage" to messageToSend
        )

        database.reference.updateChildren(updates).await()
        DataResult.Success(Unit)
    }.getOrElse { exception ->
        val appError = mapExceptionToAppError(exception)
        DataResult.Failure(appError)
    }

    override fun getMessages(conversationId: String): Flow<DataResult<List<Message>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue<Message>() }
                trySend(DataResult.Success(messages))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(DataResult.Failure(mapDatabaseErrorToAppError(error)))
                close(error.toException())
            }
        }
        messagesRef.child(conversationId).addValueEventListener(listener)
        awaitClose { messagesRef.child(conversationId).removeEventListener(listener) }
    }

    override suspend fun getMessageHistory(
        conversationId: String,
        lastMessageId: String?,
        limit: Int
    ): DataResult<List<Message>> = runCatching {
        var query = messagesRef.child(conversationId).orderByKey().limitToLast(limit)
        if (lastMessageId != null) {
            query = query.endBefore(lastMessageId)
        }
        val snapshot = query.get().await()
        val messages = snapshot.children.mapNotNull { it.getValue<Message>() }
        DataResult.Success(messages)
    }.getOrElse { exception ->
        DataResult.Failure(mapExceptionToAppError(exception))
    }

    override fun getConversations(userId: String): Flow<DataResult<List<Conversation>>> = callbackFlow {
        val userConversationsRef = database.getReference("user-conversations").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val conversations = snapshot.children.mapNotNull { it.getValue<Conversation>() }
                trySend(DataResult.Success(conversations))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(DataResult.Failure(mapDatabaseErrorToAppError(error)))
                close(error.toException())
            }
        }
        userConversationsRef.addValueEventListener(listener)
        awaitClose { userConversationsRef.removeEventListener(listener) }
    }

    override suspend fun createConversation(conversation: Conversation): DataResult<String> = runCatching {
        val conversationId = conversationsRef.push().key
            ?: throw IllegalStateException("Não foi possível gerar a chave da conversa.")
        
        val conversationToCreate = conversation.copy(conversationId = conversationId)
        
        val updates = mutableMapOf<String, Any?>()
        updates["/conversations/$conversationId"] = conversationToCreate
        conversation.participants.forEach { userId ->
            updates["/user-conversations/$userId/$conversationId"] = conversationToCreate
        }
        
        database.reference.updateChildren(updates).await()
        DataResult.Success(conversationId)
    }.getOrElse { exception ->
         DataResult.Failure(mapExceptionToAppError(exception))
    }

    override suspend fun searchUsers(query: String): DataResult<List<Any>> = runCatching {
        val snapshot = usersRef.orderByChild("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .limitToFirst(20)
            .get().await()
        
        val users = snapshot.children.mapNotNull { it.getValue<Any>() }
        DataResult.Success(users)
    }.getOrElse { exception ->
        DataResult.Failure(mapExceptionToAppError(exception))
    }

    override suspend fun updateMessageStatus(
        conversationId: String,
        messageId: String,
        status: MessageStatus
    ): DataResult<Unit> = runCatching {
        val updates = mapOf(
            "/messages/$conversationId/$messageId/status" to status,
        )
        database.reference.updateChildren(updates).await()
        DataResult.Success(Unit)
    }.getOrElse { exception ->
        DataResult.Failure(mapExceptionToAppError(exception))
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
            DatabaseError.DATA_STALE -> AppError.Database.DATA_STALE
            DatabaseError.DISCONNECTED -> AppError.Database.DISCONNECTED
            DatabaseError.EXPIRED_TOKEN -> AppError.Database.EXPIRED_TOKEN
            DatabaseError.INVALID_TOKEN -> AppError.Database.INVALID_TOKEN
            DatabaseError.MAX_RETRIES -> AppError.Database.MAX_RETRIES
            DatabaseError.OVERRIDDEN_BY_SET -> AppError.Database.OVERRIDDEN_BY_SET
            DatabaseError.UNAVAILABLE -> AppError.Database.UNAVAILABLE
            DatabaseError.WRITE_CANCELED -> AppError.Database.WRITE_CANCELED
            DatabaseError.NETWORK_ERROR -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
    }
}
