package br.com.wgc.firebasesdk.domain.repository

import br.com.wgc.firebasesdk.data.model.database.presence.PresenceState
import br.com.wgc.firebasesdk.domain.util.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * Gerencia e observa o status de presença (online/offline) de entidades.
 */
interface PresenceRepository {

    /**
     * Marca uma entidade como 'online' e configura um gatilho 'onDisconnect'
     * para marcá-la como 'offline' automaticamente se a conexão for perdida.
     *
     * @param entityType O tipo da entidade (ex: "users", "food-trucks").
     * @param entityId O ID único da entidade.
     * @return Um resultado indicando sucesso ou falha na operação.
     */
    suspend fun goOnline(entityType: String, entityId: String): DataResult<Unit>

    /**
     * Marca uma entidade como 'offline' de forma explícita.
     * Isso deve ser chamado quando o usuário faz logout ou fecha o app intencionalmente.
     *
     * @param entityType O tipo da entidade (ex: "users", "food-trucks").
     * @param entityId O ID único da entidade.
     * @return Um resultado indicando sucesso ou falha na operação.
     */
    suspend fun goOffline(entityType: String, entityId: String): DataResult<Unit>

    /**
     * Observa o estado de presença de uma entidade em tempo real.
     *
     * @param entityType O tipo da entidade a ser observada.
     * @param entityId O ID único da entidade a ser observada.
     * @return Um Flow que emite o estado de presença ('PresenceState') sempre que ele muda.
     */
    fun trackPresence(entityType: String, entityId: String): Flow<DataResult<PresenceState>>
}
