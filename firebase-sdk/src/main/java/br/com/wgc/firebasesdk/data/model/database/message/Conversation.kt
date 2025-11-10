package br.com.wgc.firebasesdk.data.model.database.message

import androidx.annotation.Keep

/**
 * Represents a chat conversation, which can be a group, one-to-one, or support chat.
 */
@Keep
data class Conversation(
    val conversationId: String = "",
    val type: ConversationType = ConversationType.ONE_TO_ONE,
    val participants: List<String> = emptyList(), // List of user/entity IDs
    val lastMessage: Message? = null,
    val unreadCount: Int = 0
)
