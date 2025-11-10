package br.com.wgc.firebasesdk.data.model.firestore

/**
 * Representa uma condição de filtro para ser usada em consultas ao Firestore.
 */
data class Filter(
    val field: String,
    val operator: Operator,
    val value: Any
)
