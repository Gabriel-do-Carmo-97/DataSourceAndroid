package br.com.wgc.firebasesdk.domain.repository

import br.com.wgc.firebasesdk.data.model.firestore.Filter
import br.com.wgc.firebasesdk.util.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * Interface genérica para operações com o Cloud Firestore.
 * Ela é projetada para funcionar com qualquer tipo de dado (usuários, produtos, etc.),
 * bastando especificar a coleção e o tipo de objeto.
 */
interface FirebaseFirestoreRepository {

    /**
     * Adiciona um novo documento a uma coleção.
     * @param T O tipo do objeto de dados a ser salvo.
     * @param collection O nome da coleção (ex: "usuarios", "produtos").
     * @param data O objeto de dados a ser salvo.
     * @param customId Um ID customizado opcional. Se nulo, o Firestore gerará um ID automaticamente.
     * @return Um `DataResult` com o ID do documento criado em caso de sucesso.
     */
    suspend fun <T : Any> addDocument(collection: String, data: T, customId: String? = null): DataResult<String>

    /**
     * Busca um único documento em uma coleção pelo seu ID.
     * @param T O tipo de objeto para o qual os dados serão convertidos.
     * @param collection O nome da coleção.
     * @param documentId O ID do documento a ser buscado.
     * @param clazz A classe do tipo T, necessária para a desserialização dos dados pelo Firestore.
     * @return Um `DataResult` com o objeto encontrado (ou nulo se não existir) em caso de sucesso.
     */
    suspend fun <T : Any> getDocument(collection: String, documentId: String, clazz: Class<T>): DataResult<T?>

    /**
     * Atualiza campos específicos de um documento existente, sem sobrescrever o objeto inteiro.
     * @param collection O nome da coleção.
     * @param documentId O ID do documento a ser atualizado.
     * @param data Um Mapa onde a chave é o nome do campo e o valor é o novo valor.
     * @return Um `DataResult` indicando o sucesso ou falha da operação.
     */
    suspend fun updateDocument(collection: String, documentId: String, data: Map<String, Any>): DataResult<Unit>

    /**
     * Deleta um documento de uma coleção.
     * @param collection O nome da coleção.
     * @param documentId O ID do documento a ser deletado.
     * @return Um `DataResult` indicando o sucesso ou falha da operação.
     */
    suspend fun deleteDocument(collection: String, documentId: String): DataResult<Unit>

    /**
     * Executa uma consulta para encontrar documentos em uma coleção que correspondem a um ou mais filtros.
     * @param T O tipo de objeto para o qual os dados serão convertidos.
     * @param collection O nome da coleção.
     * @param filters Uma lista de condições de filtro para a consulta.
     * @param clazz A classe do tipo T, necessária para a desserialização.
     * @return Um `DataResult` com a lista de objetos encontrados.
     */
    suspend fun <T : Any> findDocuments(collection: String, filters: List<Filter>, clazz: Class<T>): DataResult<List<T>>

    /**
     * Ouve as atualizações de um único documento em tempo real.
     * @return Um Flow que emite um `DataResult` com o documento sempre que ele é modificado.
     */
    fun <T : Any> listenToDocument(collection: String, documentId: String, clazz: Class<T>): Flow<DataResult<T?>>

    /**
     * Ouve as atualizações de uma consulta em uma coleção em tempo real.
     * @return Um Flow que emite um `DataResult` com a lista de documentos sempre que o resultado da consulta muda.
     */
    fun <T : Any> listenToCollection(collection: String, filters: List<Filter>, clazz: Class<T>): Flow<DataResult<List<T>>>
}

