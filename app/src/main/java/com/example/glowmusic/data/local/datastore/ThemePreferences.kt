package com.example.glowmusic.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.glowmusic.domain.model.DarkModePreference
import com.example.glowmusic.domain.model.ThemeSettings
import com.example.glowmusic.domain.model.ThemeStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferences(private val context: Context) {

    private object Keys {
        val THEME_STYLE = stringPreferencesKey("theme_style")
        val DARK_MODE = stringPreferencesKey("dark_mode_preference")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    }

    val themeSettingsFlow: Flow<ThemeSettings> = context.dataStore.data.map { prefs ->
        val styleName = prefs[Keys.THEME_STYLE] ?: ThemeStyle.DARK_NEUTRAL.name
        val darkModeName = prefs[Keys.DARK_MODE] ?: DarkModePreference.FOLLOW_SYSTEM.name

        ThemeSettings(
            themeStyle = runCatching { ThemeStyle.valueOf(styleName) }.getOrDefault(ThemeStyle.DARK_NEUTRAL),
            darkModePreference = runCatching { DarkModePreference.valueOf(darkModeName) }.getOrDefault(
                DarkModePreference.FOLLOW_SYSTEM
            ),
            useDynamicColorFromAlbumArt = prefs[Keys.DYNAMIC_COLOR] ?: true
        )
    }

    suspend fun setThemeStyle(style: ThemeStyle) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_STYLE] = style.name
        }
    }

    suspend fun setDarkModePreference(preference: DarkModePreference) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = preference.name
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DYNAMIC_COLOR] = enabled
        }
    }
}
