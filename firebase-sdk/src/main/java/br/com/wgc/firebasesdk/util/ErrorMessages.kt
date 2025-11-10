package br.com.wgc.firebasesdk.util


/**
 * Objeto centralizado que contém as mensagens de erro padrão para o usuário,
 * correspondendo aos tipos definidos em [AppError].
 *
 * A ideia é que a camada de UI chame uma função que usa este objeto
 * para traduzir um [AppError] em uma String legível para o usuário.
 */
object ErrorMessages {

    object Generic {
        const val NETWORK_ERROR = "Sem conexão com a internet. Verifique sua rede e tente novamente."
        const val UNKNOWN_ERROR = "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde."
    }

    object Auth {
        const val EMAIL_ALREADY_IN_USE = "Este endereço de e-mail já está cadastrado."
        const val WEAK_PASSWORD = "Sua senha é muito fraca. Por favor, use uma senha mais forte."
        const val INVALID_CREDENTIALS = "E-mail ou senha inválidos."
        const val REQUIRES_RECENT_LOGIN = "Esta operação é sensível e requer uma nova autenticação. Por favor, faça login novamente."
        const val USER_NOT_FOUND = "Nenhum usuário encontrado com este e-mail."
        const val INVALID_ACTION_CODE = "O link de verificação é inválido ou já expirou. Por favor, solicite um novo."
        const val EMAIL_SEND_FAILED = "Não foi possível enviar o e-mail de verificação. Tente novamente mais tarde."
        const val MULTI_FACTOR_AUTH_REQUIRED = "Esta conta requer um passo adicional de verificação para fazer login."
        const val RECAPTCHA_ACTIVITY_MISSING = "Não foi possível verificar o aplicativo. Tente novamente."
        const val WEB_OPERATION_FAILED = "A autenticação através do serviço web falhou."
        const val GENERIC_AUTH_ERROR = "Ocorreu um erro de autenticação. Por favor, tente novamente."
    }

    object Database {
        const val PERMISSION_DENIED = "Você não tem permissão para acessar estes dados."
        const val DATA_STALE = "Os dados podem estar desatualizados. Tente recarregar."
        const val OPERATION_FAILED = "A operação no banco de dados falhou."
        const val DISCONNECTED = "A operação foi cancelada por falta de conexão com a internet."
        const val EXPIRED_TOKEN = "Sua sessão expirou. Por favor, faça login novamente para continuar."
        const val INVALID_TOKEN = "Sua sessão é inválida. Por favor, faça login novamente."
        const val MAX_RETRIES = "Não foi possível completar a operação. Verifique sua conexão e tente novamente."
        const val OVERRIDDEN_BY_SET = "Suas alterações não puderam ser salvas porque os dados foram atualizados. Tente novamente."
        const val UNAVAILABLE = "O serviço está temporariamente indisponível. Tente novamente em alguns instantes."
        const val WRITE_CANCELED = "A operação de escrita foi cancelada."
    }

    object Storage {
        const val OBJECT_NOT_FOUND = "O arquivo que você está tentando acessar não foi encontrado."
        const val BUCKET_NOT_FOUND = "Erro de configuração: o local de armazenamento não foi encontrado."
        const val PROJECT_NOT_FOUND = "Erro de configuração: o projeto não foi encontrado."
        const val QUOTA_EXCEEDED = "O limite de armazenamento foi atingido. Não é possível enviar mais arquivos."
        const val PERMISSION_DENIED = "Você não tem permissão para realizar esta operação de armazenamento."
        const val UPLOAD_CANCELLED = "O envio do arquivo foi cancelado."
        const val DOWNLOAD_FAILED = "O download do arquivo falhou."
    }

    object RemoteConfig {
        const val FETCH_THROTTLED = "Muitas solicitações em um curto período. Tente novamente mais tarde."
        const val CONFIG_UPDATE_UNAVAILABLE = "Não foi possível buscar as configurações atualizadas."
    }

    object InAppMessaging {
        const val MESSAGE_DISPLAY_ERROR = "Não foi possível exibir a mensagem."
        const val IMAGE_FETCH_FAILED = "Não foi possível carregar a imagem da mensagem."
    }

    object Messaging {
        const val TOKEN_FETCH_FAILED = "Não foi possível registrar o dispositivo para notificações."
        const val SEND_MESSAGE_FAILED = "A mensagem não pôde ser enviada."
    }

    object Firestore {
        const val PERMISSION_DENIED = "Você não tem permissão para acessar este documento."
        const val DOCUMENT_NOT_FOUND = "O documento solicitado não foi encontrado."
        const val TRANSACTION_FAILED = "A operação falhou devido a um conflito de dados. Tente novamente."
        const val ABORTED = "A operação foi cancelada."
    }

    object Functions {
        const val FUNCTION_NOT_FOUND = "A operação solicitada não está disponível."
        const val INTERNAL_ERROR = "Ocorreu um erro no servidor. Tente novamente mais tarde."
        const val TIMEOUT = "A operação demorou muito para responder. Verifique sua conexão e tente novamente."
    }

    object VertexAI {
        const val RESPONSE_BLOCKED = "A resposta foi bloqueada por nossas políticas de segurança."
        const val INVALID_API_KEY = "Erro de configuração de IA."
        const val QUOTA_EXCEEDED = "Limite de solicitações de IA atingido por hoje."
        const val MODEL_UNAVAILABLE = "O modelo de IA está temporariamente indisponível."
    }
}