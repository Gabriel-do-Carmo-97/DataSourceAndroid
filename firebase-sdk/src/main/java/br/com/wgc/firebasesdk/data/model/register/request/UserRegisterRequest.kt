package br.com.wgc.firebasesdk.data.model.register.request

data class UserRegisterRequest(
    val id: String,
    val name: String,
    val lastName: String,
    val email: String,
    val password: String,
)