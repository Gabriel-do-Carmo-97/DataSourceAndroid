package br.com.wgc.firebasesdk.data.model.database.message

import androidx.annotation.Keep

/**
 * Defines the type of a conversation.
 */
@Keep
enum class ConversationType {
    /** A one-to-one chat between two individual users. */
    ONE_TO_ONE,

    /** A chat with multiple participants. */
    GROUP,

    /** A chat between a user and a business/support agent. */
    SUPPORT
}
