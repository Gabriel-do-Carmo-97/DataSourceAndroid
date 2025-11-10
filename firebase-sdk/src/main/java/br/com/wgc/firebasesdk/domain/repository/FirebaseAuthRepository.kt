package br.com.wgc.firebasesdk.domain.repository

import android.net.Uri
import br.com.wgc.firebasesdk.data.model.auth.login.request.LoginRequest
import br.com.wgc.firebasesdk.data.model.auth.login.response.LoginResponse
import br.com.wgc.firebasesdk.data.model.auth.register.request.UserRegisterRequest
import br.com.wgc.firebasesdk.data.model.auth.register.response.UserRegisterResponse
import br.com.wgc.firebasesdk.domain.util.DataResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthRepository {

    suspend fun registerEmailWithPassword(userRequest: UserRegisterRequest): DataResult<UserRegisterResponse>
    suspend fun loginEmailWithPassword(loginRequest: LoginRequest): DataResult<LoginResponse>
    suspend fun getCurrentUser(): DataResult<FirebaseUser?> // Modificado para retornar seu modelo
    suspend fun signOut(): DataResult<Unit>
    suspend fun sendPasswordResetEmail(email: String): DataResult<Unit>



    suspend fun updateProfile(name: String?, photoUri: Uri?): DataResult<Unit> // Modificado
    suspend fun updateEmail(newEmail: String): DataResult<Unit>
    suspend fun updatePassword(newPassword: String): DataResult<Unit>
    suspend fun delete(): DataResult<Unit>
    suspend fun reauthenticate(password: String): DataResult<Unit> // Modificado
    suspend fun sendEmailVerification(): DataResult<Unit>


    suspend fun linkWithCredential(email: String, credential: AuthCredential): DataResult<Unit>
    suspend fun unlink(providerId: String): DataResult<Unit>
//    suspend fun loginWithGoogle(idToken: String)
//    suspend fun loginWithFacebook(accessToken: String)
//    suspend fun loginWithTwitter(authToken: String, authSecret: String)
//    suspend fun loginWithGithub(accessToken: String)
//    suspend fun loginWithApple(idToken: String)
//    suspend fun loginWithMicrosoft(accessToken: String)
//    suspend fun loginWithYahoo(accessToken: String)
//    suspend fun loginWithPlayGames(accessToken: String)
//    suspend fun loginWithPhoneNumber(number: String)
    suspend fun loginAnonymously(): DataResult<LoginResponse>
    suspend fun isUserLogged(): DataResult<Boolean>
    suspend fun loginWithCredential(credential: AuthCredential): DataResult<LoginResponse>
}
