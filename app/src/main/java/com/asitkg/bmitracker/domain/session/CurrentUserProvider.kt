package com.asitkg.bmitracker.domain.session

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supplies the uid that owns newly created profiles.
 *
 * Profiles are scoped to an account, but the details form is being built before
 * the auth screens exist. Until Phase 1 lands, an unauthenticated run falls back
 * to [LOCAL_UID] so the form is usable standalone; once sign-in works the real
 * uid is returned and nothing downstream changes.
 */
@Singleton
class CurrentUserProvider @Inject constructor() {

    fun currentUid(): String =
        FirebaseAuth.getInstance().currentUser?.uid ?: LOCAL_UID

    companion object {
        const val LOCAL_UID = "local-user"
    }
}
