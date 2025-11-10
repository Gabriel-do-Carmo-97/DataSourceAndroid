package br.com.wgc.firebasesdk.data.repository

import android.net.Uri
import android.util.Log
import br.com.wgc.firebasesdk.data.model.auth.login.request.LoginRequest
import br.com.wgc.firebasesdk.data.model.auth.login.response.LoginResponse
import br.com.wgc.firebasesdk.data.model.auth.register.request.UserRegisterRequest
import br.com.wgc.firebasesdk.data.model.auth.register.response.UserRegisterResponse
import br.com.wgc.firebasesdk.domain.repository.FirebaseAuthRepository
import br.com.wgc.firebasesdk.util.AppError
import br.com.wgc.firebasesdk.util.DataResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

internal class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : FirebaseAuthRepository {

    companion object {
        const val WEB_CLIENT_ID = "132508821115-k8p2mn0m40mq0vphn7lkk3sscsn15bmd.apps.googleusercontent.com"
    }

    override suspend fun registerEmailWithPassword(
        userRequest: UserRegisterRequest
    ): DataResult<UserRegisterResponse> = runCatching {
        val authResult = auth.createUserWithEmailAndPassword(
            userRequest.email,
            userRequest.password
        ).await()

        authResult.user?.sendEmailVerification()?.await()
        DataResult.Success(
            UserRegisterResponse(
                id = authResult.user?.uid.toString(),
                name = userRequest.name + " " + userRequest.lastName,
                method = authResult.credential?.signInMethod.toString(),
                provider = authResult.credential?.provider.toString(),
                isAnonymous = authResult.user?.isAnonymous ?: false,
                isEmailVerified = authResult.user?.isEmailVerified ?: false,
                isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            )
        )
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha no registro: ${exception.message}", exception)
        val error: AppError = when (exception) {
            is FirebaseAuthWeakPasswordException -> AppError.Auth.WEAK_PASSWORD
            is FirebaseAuthUserCollisionException -> AppError.Auth.EMAIL_ALREADY_IN_USE
            is FirebaseAuthInvalidCredentialsException -> AppError.Auth.INVALID_CREDENTIALS
            is IOException -> AppError.Generic.NETWORK_ERROR
            is FirebaseAuthException -> AppError.Auth.GENERIC_AUTH_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        } as AppError
        DataResult.Failure(error)
    }


    override suspend fun loginEmailWithPassword(
        loginRequest: LoginRequest
    ) = runCatching {
        val authResult = auth.signInWithEmailAndPassword(
            loginRequest.email,
            loginRequest.password
        ).await()
        DataResult.Success(LoginResponse(authResult.user?.uid.toString()))
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha no login: ${exception.message}", exception)
        val error: AppError = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> AppError.Auth.INVALID_CREDENTIALS
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        } as AppError
        DataResult.Failure(error)

    }


    override suspend fun getCurrentUser() = runCatching {
        DataResult.Success(auth.currentUser)
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha ao obter usuário atual: ${exception.message}", exception)
        val error: AppError = when (exception) {
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        } as AppError
        DataResult.Failure(error)
    }

    override suspend fun isUserLogged() = runCatching {
        DataResult.Success(auth.currentUser != null)
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha ao obter usuário atual: ${exception.message}", exception)
        val error: AppError = when (exception) {
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        } as AppError
        DataResult.Failure(error)
    }


    override suspend fun signOut() = runCatching {
        auth.signOut()
        DataResult.Success(Unit)
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha ao obter usuário atual: ${exception.message}", exception)
        DataResult.Failure(AppError.Generic.UNKNOWN_ERROR)
    }

    override suspend fun updateProfile(
        name: String?,
        photoUri: Uri?
    ): DataResult<Unit> = runCatching {
        val user = auth.currentUser
            ?: throw FirebaseAuthInvalidUserException("ERROR_USER_NOT_FOUND", "Nenhum usuário logado.")

        val request = UserProfileChangeRequest.Builder().apply {
            name?.let { setDisplayName(it) }
            photoUri?.let { setPhotoUri(it) }
        }.build()

        user.updateProfile(request).await()
        DataResult.Success(Unit)
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha ao atualizar perfil: ${exception.message}", exception)
        val error = when(exception) {
            is FirebaseAuthInvalidUserException -> AppError.Auth.USER_NOT_FOUND
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
        DataResult.Failure(error)
    }

    override suspend fun updateEmail(newEmail: String): DataResult<Unit> = runCatching {
        auth.currentUser!!.verifyBeforeUpdateEmail(newEmail).await()
        DataResult.Success(Unit)
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha ao atualizar e-mail: ${exception.message}", exception)
        val error = when (exception) {
            is FirebaseAuthRecentLoginRequiredException -> AppError.Auth.REQUIRES_RECENT_LOGIN
            is FirebaseAuthUserCollisionException -> AppError.Auth.EMAIL_ALREADY_IN_USE
            is FirebaseAuthInvalidCredentialsException -> AppError.Auth.INVALID_CREDENTIALS // E-mail mal formatado
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
        DataResult.Failure(error)
    }

    override suspend fun updatePassword(newPassword: String): DataResult<Unit> = runCatching {
        auth.currentUser!!.updatePassword(newPassword).await()
        DataResult.Success(Unit)
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha ao atualizar senha: ${exception.message}", exception)
        val error = when (exception) {
            is FirebaseAuthRecentLoginRequiredException -> AppError.Auth.REQUIRES_RECENT_LOGIN
            is FirebaseAuthWeakPasswordException -> AppError.Auth.WEAK_PASSWORD
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
        DataResult.Failure(error)
    }

    override suspend fun sendEmailVerification(): DataResult<Unit> = runCatching {
        auth.currentUser!!.sendEmailVerification().await()
        DataResult.Success(Unit)
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha ao enviar verificação de e-mail: ${exception.message}", exception)
        val error = when (exception) {
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
        DataResult.Failure(error)
    }

    override suspend fun sendPasswordResetEmail(email: String): DataResult<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
        DataResult.Success(Unit)
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha ao enviar e-mail de redefinição: ${exception.message}", exception)
        val error = when (exception) {
            is FirebaseAuthInvalidUserException -> AppError.Auth.USER_NOT_FOUND
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
        DataResult.Failure(error)
    }

    override suspend fun delete(): DataResult<Unit> = runCatching {
        auth.currentUser!!.delete().await()
        DataResult.Success(Unit)
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha ao deletar usuário: ${exception.message}", exception)
        val error = when (exception) {
            is FirebaseAuthRecentLoginRequiredException -> AppError.Auth.REQUIRES_RECENT_LOGIN
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
        DataResult.Failure(error)
    }

    override suspend fun reauthenticate(password: String): DataResult<Unit> = runCatching {
        val user = auth.currentUser!!
        val credential = EmailAuthProvider.getCredential(user.email!!, password)
        user.reauthenticate(credential).await()
        DataResult.Success(Unit)
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha na reautenticação: ${exception.message}", exception)
        val error = when(exception) {
            is FirebaseAuthInvalidCredentialsException -> AppError.Auth.INVALID_CREDENTIALS
            is FirebaseAuthInvalidUserException -> AppError.Auth.USER_NOT_FOUND
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
        DataResult.Failure(error)
    }

    override suspend fun linkWithCredential(email: String, credential: AuthCredential) = runCatching {
        auth.currentUser!!.linkWithCredential(credential).await()
        DataResult.Success(Unit)
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha ao vincular credencial: ${exception.message}", exception)
        val error = when (exception) {
            is FirebaseAuthUserCollisionException -> AppError.Auth.EMAIL_ALREADY_IN_USE // A credencial já está em uso por outra conta
            is FirebaseAuthInvalidUserException -> AppError.Auth.USER_NOT_FOUND
            is FirebaseAuthRecentLoginRequiredException -> AppError.Auth.REQUIRES_RECENT_LOGIN
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
        DataResult.Failure(error)
    }

    override suspend fun unlink(providerId: String): DataResult<Unit> = runCatching {
        auth.currentUser!!.unlink(providerId).await()
        DataResult.Success(Unit)
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha ao desvincular provedor: ${exception.message}", exception)
        val error = when (exception) {
            // Unlink pode falhar se for o único provedor de login do usuário.
            is FirebaseAuthInvalidUserException -> AppError.Auth.USER_NOT_FOUND
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
        DataResult.Failure(error)
    }

    override suspend fun loginWithCredential(credential: AuthCredential): DataResult<LoginResponse> = runCatching {
        val authResult = auth.signInWithCredential(credential).await()
        DataResult.Success(LoginResponse(authResult.user!!.uid))
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha no login com credencial: ${exception.message}", exception)
        val error = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> AppError.Auth.INVALID_CREDENTIALS
            is FirebaseAuthInvalidUserException -> AppError.Auth.USER_NOT_FOUND
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
        DataResult.Failure(error)
    }

    override suspend fun loginAnonymously(): DataResult<LoginResponse> = runCatching {
        val authResult = auth.signInAnonymously().await()
        DataResult.Success(LoginResponse(authResult.user!!.uid))
    }.getOrElse { exception ->
        Log.e("AuthRepoImpl", "Falha no login anônimo: ${exception.message}", exception)
        val error = when (exception) {
            is IOException -> AppError.Generic.NETWORK_ERROR
            else -> AppError.Generic.UNKNOWN_ERROR
        }
        DataResult.Failure(error)
    }

//    override suspend fun loginWithGoogle(idToken: String) {
//        val googleIdOptions = GetGoogleIdOption.Builder()
//            .setServerClientId(WEB_CLIENT_ID)
//            .setFilterByAuthorizedAccounts(true)
//            .build()
//
//        val request = GetCredentialRequest.Builder()
//            .addCredentialOption(googleIdOptions)
//            .build()
//
//    }
//
//    override suspend fun loginWithFacebook(accessToken: String) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun loginWithTwitter(authToken: String, authSecret: String) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun loginWithGithub(accessToken: String) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun loginWithApple(idToken: String) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun loginWithMicrosoft(accessToken: String) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun loginWithYahoo(accessToken: String) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun loginWithPlayGames(accessToken: String) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun loginWithPhoneNumber(number: String) {
//        TODO("Not yet implemented")
//    }
}