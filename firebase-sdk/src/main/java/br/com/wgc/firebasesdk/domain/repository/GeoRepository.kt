package br.com.wgc.firebasesdk.domain.repository

import br.com.wgc.firebasesdk.data.model.database.geo.Location
import br.com.wgc.firebasesdk.domain.util.DataResult
import kotlinx.coroutines.flow.Flow

internal interface GeoRepository {

    /**
     * Updates the geographic location for a specific entity (e.g., user, food truck).
     * @param entityType The type of entity, used as the parent node (e.g., "users", "food-trucks").
     * @param entityId The unique identifier for the entity within its type.
     * @param location The new location data for the entity.
     */
    suspend fun updateLocation(
        entityType: String,
        entityId: String,
        location: Location
    ) : DataResult<String>

    /**
     * Tracks the location of a specific entity in real-time.
     * @param entityType The type of entity to track.
     * @param entityId The unique identifier for the entity to track.
     * @return A Flow that emits the entity's Location whenever it changes.
     */
    fun trackLocation(entityType: String, entityId: String): Flow<DataResult<Location>>
}
