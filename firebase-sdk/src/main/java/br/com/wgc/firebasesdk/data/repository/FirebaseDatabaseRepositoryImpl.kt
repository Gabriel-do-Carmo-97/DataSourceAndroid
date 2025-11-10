package br.com.wgc.firebasesdk.data.repository

import br.com.wgc.firebasesdk.domain.repository.FirebaseDatabaseRepository
import br.com.wgc.firebasesdk.domain.repository.GeoRepository
import br.com.wgc.firebasesdk.domain.repository.MessageRepository
import br.com.wgc.firebasesdk.domain.repository.PresenceRepository
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

internal class FirebaseDatabaseRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
) : FirebaseDatabaseRepository {

    override fun messages(): MessageRepository {
        return MessageRepositoryImpl(database)
    }

    override fun geo(): GeoRepository {
        return GeoRepositoryImpl(database)
    }

    override fun presence(): PresenceRepository {
        return PresenceRepositoryImpl(database)
    }
}
