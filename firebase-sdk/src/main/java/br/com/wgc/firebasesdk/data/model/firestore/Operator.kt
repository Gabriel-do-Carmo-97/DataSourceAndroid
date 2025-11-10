package br.com.wgc.firebasesdk.data.model.firestore


/**
 * Enumera os operadores de consulta suportados pelo repositório.
 */
enum class Operator {
    EQUAL_TO,
    NOT_EQUAL_TO,
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUAL_TO,
    LESS_THAN_OR_EQUAL_TO,
    ARRAY_CONTAINS,
    ARRAY_CONTAINS_ANY,
    IN,
    NOT_IN
}