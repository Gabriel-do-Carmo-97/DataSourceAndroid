package br.com.wgc.firebasesdk.domain.usecase

import br.com.wgc.firebasesdk.data.model.auth.login.request.LoginRequest
import br.com.wgc.firebasesdk.data.model.auth.login.response.LoginResponse
import br.com.wgc.firebasesdk.domain.repository.FirebaseAuthRepository
import br.com.wgc.firebasesdk.domain.repository.FirebaseDatabaseRepository
import br.com.wgc.firebasesdk.domain.util.DataResult
import javax.inject.Inject

/**
 * Caso de uso para executar a lógica de login de um usuário.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val databaseRepository: FirebaseDatabaseRepository,
) {
    /**
     * Executa o caso de uso de login.
     * Permite que a classe seja chamada como uma função (ex: loginUseCase(request)).
     *
     * @return Um DataResult contendo a resposta de LoginResponse em caso de sucesso
     * ou um AppError em caso de falha, que será então usado pelo ViewModel para
     * atualizar o estado da UI.
     */
    suspend operator fun invoke(request: LoginRequest): DataResult<LoginResponse> {
        val authResult = authRepository.loginEmailWithPassword(request)
        authResult as? DataResult.Failure
        val presenceResult = databaseRepository
            .presence()
            .goOnline(
                entityType = "users",
                entityId = (authResult as DataResult.Success).data.uuid
            )
        if (presenceResult is DataResult.Failure) {
            authRepository.signOut()
            return DataResult.Failure(presenceResult.error)
        }
        return authResult
    }

    /**
     * Executa o caso de uso de login anônimo.
     * Permite que a classe seja chamada como uma função (ex: loginAnonymouslyUseCase()).
     */
    suspend fun loginAnonymous(): DataResult<LoginResponse> {
        val authResult = authRepository.loginAnonymously()
        authResult as? DataResult.Failure
        val presenceResult = databaseRepository.presence().goOnline(
            entityType = "users",
            entityId = (authResult as DataResult.Success).data.uuid
        )
        if (presenceResult is DataResult.Failure) {
            authRepository.signOut()
            return DataResult.Failure(presenceResult.error)
        }
        return authResult
    }
}
