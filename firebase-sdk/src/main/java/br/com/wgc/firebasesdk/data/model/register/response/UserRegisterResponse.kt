package br.com.wgc.firebasesdk.data.model.register.response

data class UserRegisterResponse(
    val id: String,
    val name: String,
    val method: String,
    val provider: String,
    val isAnonymous: Boolean,
    val isEmailVerified: Boolean,
    val isNewUser: Boolean,

    )