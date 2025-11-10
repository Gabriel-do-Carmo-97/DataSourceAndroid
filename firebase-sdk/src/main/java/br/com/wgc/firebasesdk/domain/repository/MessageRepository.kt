package br.com.wgc.firebasesdk.domain.repository


import br.com.wgc.firebasesdk.data.model.database.message.Conversation
import br.com.wgc.firebasesdk.data.model.database.message.Message
import br.com.wgc.firebasesdk.data.model.database.message.MessageStatus
import br.com.wgc.firebasesdk.domain.util.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * Gerencia todas as operações relacionadas a mensagens, incluindo chats individuais, em grupo e de suporte.
 */
interface MessageRepository {

    /**
     * Envia uma mensagem para uma conversa específica.
     *
     * @param conversationId O ID da conversa para a qual a mensagem será enviada.
     * @param message O objeto da mensagem a ser enviada.
     * @return Um resultado indicando sucesso ou falha.
     */
    suspend fun sendMessage(conversationId: String, message: Message): DataResult<Unit>

    /**
     * Ouve as mensagens recebidas em uma conversa específica em tempo real.
     *
     * @param conversationId O ID da conversa a ser ouvida.
     * @return Um Flow que emite a lista de todas as mensagens na conversa sempre que há uma atualização.
     */
    fun getMessages(conversationId: String): Flow<DataResult<List<Message>>>

    /**
     * Recupera um histórico paginado de mensagens para uma conversa.
     *
     * @param conversationId O ID da conversa.
     * @param lastMessageId O ID da última mensagem já buscada (para paginação). Nulo para começar das mais recentes.
     * @param limit O número máximo de mensagens a serem recuperadas.
     * @return Um resultado contendo a lista de mensagens ou um erro.
     */
    suspend fun getMessageHistory(conversationId: String, lastMessageId: String?, limit: Int = 50): DataResult<List<Message>>

    /**
     * Obtém uma lista em tempo real de todas as conversas de um usuário específico.
     *
     * @param userId O ID do usuário cujas conversas devem ser buscadas.
     * @return Um Flow que emite a lista de conversas sempre que ela muda.
     */
    fun getConversations(userId: String): Flow<DataResult<List<Conversation>>>

    /**
     * Cria uma nova conversa.
     *
     * @param conversation O objeto da conversa a ser criada.
     * @return Um resultado contendo o ID da conversa recém-criada ou um erro.
     */
    suspend fun createConversation(conversation: Conversation): DataResult<String>

    /**
     * Busca por usuários ou participantes no contexto de mensagens.
     *
     * @param query A consulta de busca (ex: nome, e-mail).
     * @return Um resultado contendo uma lista de usuários/participantes encontrados ou um erro.
     */
    suspend fun searchUsers(query: String): DataResult<List<Any>> // Usando 'Any' para um modelo de usuário genérico

    /**
     * Atualiza o status de uma mensagem específica (ex: de ENVIADA para ENTREGUE ou LIDA).
     *
     * @param conversationId O ID da conversa que contém a mensagem.
     * @param messageId O ID da mensagem a ser atualizada.
     * @param status O novo status da mensagem.
     * @return Um resultado indicando sucesso ou falha.
     */
    suspend fun updateMessageStatus(conversationId: String, messageId: String, status: MessageStatus): DataResult<Unit>
}
