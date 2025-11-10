package br.com.wgc.firebasesdk.data.model.database.geo

import androidx.annotation.Keep

/**
 * Represents a user's geographic coordinates.
 * Used to be saved to and retrieved from the Firebase Realtime Database.
 */
@Keep
data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
