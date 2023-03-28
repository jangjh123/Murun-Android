package com.jh.murun.data.data_store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreManager @Inject constructor(private val context: Context) {
    private val Context.instance: DataStore<Preferences> by preferencesDataStore(KEY_DATASTORE)

    fun getToSkipOnBoarding(): Flow<Boolean> {
        return context.instance.data.map { preferences ->
            preferences[booleanPreferencesKey(KEY_SKIP_ON_BOARDING)] ?: false
        }
    }

    suspend fun setToSkipOnBoarding() {
        context.instance.edit { preferences ->
            preferences[booleanPreferencesKey(KEY_SKIP_ON_BOARDING)] = true
        }
    }

    companion object {
        private const val KEY_DATASTORE = "murun"
        private const val KEY_SKIP_ON_BOARDING = "skip_on_boarding"
    }
}