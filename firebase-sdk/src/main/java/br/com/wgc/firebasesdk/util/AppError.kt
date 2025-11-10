package br.com.wgc.firebasesdk.util

sealed interface AppError {


    /**
     * Erros genéricos que podem ocorrer em qualquer operação.
     */
    enum class Generic : AppError {
        NETWORK_ERROR,
        UNKNOWN_ERROR // Corrigido o erro de digitação de "UNKNOW"
    }

    /**
     * Erros específicos do serviço de Autenticação (Authentication).
     */
    enum class Auth : AppError {
        // --- Erros de Criação e Conflito de Usuário ---
        /**
         * Lançado ao tentar criar um usuário com um e-mail que já existe.
         * Mapeado de: `FirebaseAuthUserCollisionException`
         */
        EMAIL_ALREADY_IN_USE,
        /**
         * Lançado ao usar uma senha fraca (geralmente menos de 6 caracteres).
         * Mapeado de: `FirebaseAuthWeakPasswordException`
         */
        WEAK_PASSWORD,

        // --- Erros de Credenciais e Validação ---
        /**
         * Lançado quando as credenciais (e-mail/senha) fornecidas são inválidas.
         * Mapeado de: `FirebaseAuthInvalidCredentialsException`
         */
        INVALID_CREDENTIALS,
        /**
         * Lançado ao tentar uma operação em um usuário que não existe ou está desabilitado.
         * Mapeado de: `FirebaseAuthInvalidUserException`
         */
        USER_NOT_FOUND,

        // --- Erros de Fluxos de Verificação (Links, Códigos) ---
        /**
         * Lançado quando um link/código de ação (ex: redefinir senha) é inválido ou expirou.
         * Mapeado de: `FirebaseAuthActionCodeException`
         */
        INVALID_ACTION_CODE,
        /**
         * Lançado quando há uma falha ao tentar enviar um e-mail (ex: de redefinição de senha).
         * Mapeado de: `FirebaseAuthEmailException`
         */
        EMAIL_SEND_FAILED,

        // --- Erros de Segurança e Sessão ---
        /**
         * Lançado para operações sensíveis que exigem que o usuário tenha logado recentemente.
         * Mapeado de: `FirebaseAuthRecentLoginRequiredException`
         */
        REQUIRES_RECENT_LOGIN,

        // --- Erros de Autenticação Multifator (MFA) ---
        /**
         * Lançado quando o login requer um segundo fator de autenticação.
         * Mapeado de: `FirebaseAuthMultiFactorException`
         */
        MULTI_FACTOR_AUTH_REQUIRED,

        // --- Erros de Configuração e Ambiente (Recaptcha, Web) ---
        /**
         * Lançado quando uma verificação reCAPTCHA é necessária, mas a Activity não está disponível.
         * Mapeado de: `FirebaseAuthMissingActivityForRecaptchaException`
         */
        RECAPTCHA_ACTIVITY_MISSING,
        /**
         * Lançado quando uma operação web (como login via provedor) falha.
         * Mapeado de: `FirebaseAuthWebException`
         */
        WEB_OPERATION_FAILED,

        // --- Erro Genérico de Autenticação ---
        /**
         * Um erro genérico do Firebase Auth que não se encaixa nas categorias específicas.
         * Mapeado de: `FirebaseAuthException`
         */
        GENERIC_AUTH_ERROR
    }

    /**
     * Erros específicos do serviço de Banco de Dados em Tempo Real (Realtime Database).
     */
    enum class Database : AppError {
        PERMISSION_DENIED,
        DATA_STALE,
        OPERATION_FAILED,
        DISCONNECTED,
        EXPIRED_TOKEN,
        INVALID_TOKEN,
        MAX_RETRIES,
        OVERRIDDEN_BY_SET,
        UNAVAILABLE,
        WRITE_CANCELED
    }

    /**
     * Erros específicos do serviço de Armazenamento (Cloud Storage).
     */
    enum class Storage : AppError {
        OBJECT_NOT_FOUND,
        BUCKET_NOT_FOUND,
        PROJECT_NOT_FOUND,
        QUOTA_EXCEEDED,
        PERMISSION_DENIED,
        UPLOAD_CANCELLED,
        DOWNLOAD_FAILED,
    }

    /**
     * Erros específicos do serviço de Configuração Remota (Remote Config).
     */
    enum class Config : AppError {
        FETCH_THROTTLED,
        CONFIG_UPDATE_UNAVAILABLE,
    }

    /**
     * Erros específicos do serviço de Mensagens no App (In-App Messaging).
     */
    enum class InAppMessaging : AppError {
        MESSAGE_DISPLAY_ERROR,
        IMAGE_FETCH_FAILED,
    }

    /**
     * Erros específicos do serviço de Mensagens na Nuvem (Cloud Messaging).
     */
    enum class Messaging : AppError {
        TOKEN_FETCH_FAILED,
        SEND_MESSAGE_FAILED,
    }

    /**
     * Erros específicos do serviço de Banco de Dados (Cloud Firestore).
     */
    enum class Firestore : AppError {
        PERMISSION_DENIED,
        DOCUMENT_NOT_FOUND,
        TRANSACTION_FAILED,
        ABORTED,
    }

    /**
     * Erros específicos do serviço de Monitoramento de Desempenho (Performance Monitoring).
     * (Nota: Geralmente não se modela erros para o Performance, pois ele é passivo).
     */
    // enum class Performance : AppError {}

    /**
     * Erros específicos do serviço de Relatórios de Falhas (Crashlytics).
     * (Nota: Similar ao Performance, não é comum modelar erros para o Crashlytics).
     */
    // enum class Crashlytics : AppError {}

    /**
     * Erros específicos do serviço de Funções na Nuvem (Cloud Functions).
     */
    enum class Functions : AppError {
        FUNCTION_NOT_FOUND,
        INTERNAL_ERROR,
        TIMEOUT,
    }

    /**
     * Erros específicos da IA Generativa (Vertex AI / Gemini).
     */
    enum class VertexAI : AppError {
        RESPONSE_BLOCKED,
        INVALID_API_KEY,
        QUOTA_EXCEEDED,
        MODEL_UNAVAILABLE,
    }
}