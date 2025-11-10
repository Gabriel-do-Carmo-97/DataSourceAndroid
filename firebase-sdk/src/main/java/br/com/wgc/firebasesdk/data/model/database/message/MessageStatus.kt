package br.com.wgc.firebasesdk.data.model.database.message

import androidx.annotation.Keep

/**
 * Represents the delivery status of a message.
 */
@Keep
enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}
