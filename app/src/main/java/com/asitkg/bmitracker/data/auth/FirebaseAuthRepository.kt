package com.asitkg.bmitracker.data.auth

import com.asitkg.bmitracker.domain.repository.AuthRepository
import com.asitkg.bmitracker.domain.repository.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    /**
     * Firebase persists the session to disk itself, so this emits a uid
     * immediately on a cold start for an already-signed-in user — no manual
     * token storage is involved.
     */
    override val authState: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override val currentUid: String?
        get() = firebaseAuth.currentUser?.uid

    override val currentEmail: String?
        get() = firebaseAuth.currentUser?.email

    override suspend fun signIn(email: String, password: String): AuthResult = runAuth {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signUp(email: String, password: String): AuthResult = runAuth {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult = runAuth {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
    }

    override suspend fun sendPasswordReset(email: String): AuthResult = runAuth {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override fun signOut() = firebaseAuth.signOut()

    private inline fun runAuth(block: () -> Unit): AuthResult = try {
        block()
        AuthResult.Success
    } catch (e: FirebaseAuthException) {
        AuthResult.Failure(messageFor(e))
    } catch (e: IOException) {
        AuthResult.Failure("No internet connection. Check your network and try again.")
    } catch (e: Exception) {
        AuthResult.Failure(e.message ?: "Something went wrong. Please try again.")
    }

    /**
     * Firebase error codes are not user-facing. Note that with email
     * enumeration protection enabled — the current default — a wrong password
     * and an unknown account both surface as INVALID_CREDENTIAL, deliberately,
     * so the message must not distinguish them.
     */
    private fun messageFor(e: FirebaseAuthException): String = when (e.errorCode) {
        "ERROR_INVALID_EMAIL" -> "That email address isn't valid."
        "ERROR_INVALID_CREDENTIAL",
        "ERROR_WRONG_PASSWORD",
        "ERROR_USER_NOT_FOUND",
        -> "Email or password is incorrect."

        "ERROR_EMAIL_ALREADY_IN_USE" -> "An account already exists with that email."
        "ERROR_WEAK_PASSWORD" -> "That password is too weak. Choose a stronger one."
        "ERROR_USER_DISABLED" -> "This account has been disabled."
        "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Try again in a few minutes."
        "ERROR_NETWORK_REQUEST_FAILED" -> "No internet connection. Check your network and try again."
        "ERROR_OPERATION_NOT_ALLOWED" -> "This sign-in method is not enabled for this project."
        else -> e.localizedMessage ?: "Authentication failed. Please try again."
    }
}
