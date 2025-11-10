package br.com.wgc.firebasesdk.data.model.database.presence

import androidx.annotation.Keep

/**
 * Representa o estado de presença de uma entidade (online/offline).
 */
@Keep
data class PresenceState(
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis() // Timestamp da última mudança
)
