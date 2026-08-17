package com.asitkg.bmitracker.domain.repository

import kotlinx.coroutines.flow.Flow

/** Outcome of an auth call, with a message already fit to show the user. */
sealed interface AuthResult {

    data object Success : AuthResult

    data class Failure(val message: String) : AuthResult
}

interface AuthRepository {

    /** Emits the signed-in uid, or null when signed out. Backed by Firebase's
     *  persisted session, which is what makes login survive app restarts. */
    val authState: Flow<String?>

    val currentUid: String?

    val currentEmail: String?

    val isSignedIn: Boolean get() = currentUid != null

    suspend fun signIn(email: String, password: String): AuthResult

    suspend fun signUp(email: String, password: String): AuthResult

    /** @param idToken a Google ID token obtained via Credential Manager. */
    suspend fun signInWithGoogle(idToken: String): AuthResult

    suspend fun sendPasswordReset(email: String): AuthResult

    fun signOut()
}
