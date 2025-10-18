package com.example.prodhackathonspb.login.data

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.spght.encryptedprefs.EncryptedSharedPreferences
import dev.spght.encryptedprefs.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenHolder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey(context)
    private val prefs = EncryptedSharedPreferences(
        context = context,
        fileName = "secure_token",
        masterKey = masterKey
    )

    fun saveToken(token: String) {
        prefs.edit { putString("auth_token", token) }
    }

    fun getToken(): String? = prefs.getString("auth_token", null)

    fun clearToken() = prefs.edit { remove("auth_token") }
}