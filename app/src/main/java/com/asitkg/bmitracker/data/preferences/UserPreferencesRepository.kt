package com.asitkg.bmitracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * Small, non-relational state that outlives a session.
 *
 * The active profile lives here rather than in Room because it is a UI
 * selection, not user data — clearing it must never risk the profiles
 * themselves.
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val ACTIVE_PROFILE_ID = longPreferencesKey("active_profile_id")
    }

    /** Null when no profile has been selected yet. */
    val activeProfileId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[Keys.ACTIVE_PROFILE_ID]?.takeIf { it > 0L }
    }

    suspend fun setActiveProfileId(profileId: Long) {
        context.dataStore.edit { prefs -> prefs[Keys.ACTIVE_PROFILE_ID] = profileId }
    }

    suspend fun clearActiveProfile() {
        context.dataStore.edit { prefs -> prefs.remove(Keys.ACTIVE_PROFILE_ID) }
    }
}
