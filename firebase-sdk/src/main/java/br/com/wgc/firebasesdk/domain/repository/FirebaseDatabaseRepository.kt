package br.com.wgc.firebasesdk.domain.repository

interface FirebaseDatabaseRepository {
    fun messages(): MessageRepository
    fun geo(): GeoRepository
    fun presence(): PresenceRepository
}
