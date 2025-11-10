package br.com.wgc.firebasesdk.domain.usecase

import br.com.wgc.firebasesdk.domain.repository.FirebaseAuthRepository
import br.com.wgc.firebasesdk.domain.repository.FirebaseDatabaseRepository
import br.com.wgc.firebasesdk.domain.repository.FirebaseFirestoreRepository
import javax.inject.Inject

class LoginUSeCase @Inject constructor(
    private val auth: FirebaseAuthRepository,
    private val database: FirebaseDatabaseRepository,
    private val firestore: FirebaseFirestoreRepository
){

}