package com.example.prodhackathonspb.login.data

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "secure_token_store")

@Singleton
class TokenHolder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tokenKey = stringPreferencesKey("auth_token")
    private val keyAlias = "auth_token_key"

    companion object {
        private const val MIN_SECURE_API = Build.VERSION_CODES.M // API 23
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 128
    }

    /**
     * Проверяет доступность безопасного хранилища
     */
    private fun isSecureStorageAvailable(): Boolean {
        return Build.VERSION.SDK_INT >= MIN_SECURE_API
    }

    /**
     * Получает или создает секретный ключ в Android Keystore
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
            load(null)
        }

        // Проверяем существование ключа
        if (keyStore.containsAlias(keyAlias)) {
            val entry = keyStore.getEntry(keyAlias, null)
            if (entry is KeyStore.SecretKeyEntry) {
                return entry.secretKey
            }
        }

        // Создаем новый ключ
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEY_STORE
        )

        val keyGenSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(keyGenSpec)
        return keyGenerator.generateKey()
    }

    /**
     * Шифрует текст с использованием AES/GCM
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun encrypt(text: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())

        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

        // Комбинируем IV и зашифрованные данные
        val combined = iv + encryptedBytes

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * Расшифровывает текст
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun decrypt(encryptedText: String): String {
        val combined = Base64.decode(encryptedText, Base64.NO_WRAP)

        // Разделяем IV и зашифрованные данные
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val encryptedBytes = combined.copyOfRange(GCM_IV_LENGTH, combined.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)

        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    /**
     * Сохраняет токен в зашифрованном виде
     */
    suspend fun saveToken(token: String) {
        if (!isSecureStorageAvailable()) {
            throw UnsupportedOperationException(
                "Secure token storage requires Android 6.0+ (API 23)"
            )
        }

        context.dataStore.edit { preferences ->
            val encryptedToken = encrypt(token)
            preferences[tokenKey] = encryptedToken
        }
    }

    /**
     * Получает токен и расшифровывает его
     */
    suspend fun getToken(): String? {
        if (!isSecureStorageAvailable()) {
            return null
        }

        return try {
            context.dataStore.data.map { preferences ->
                preferences[tokenKey]?.let { encryptedToken ->
                    decrypt(encryptedToken)
                }
            }.first()
        } catch (e: Exception) {
            // Если произошла ошибка расшифровки - удаляем поврежденный токен
            clearToken()
            null
        }
    }

    /**
     * Возвращает Flow с токеном для реактивного использования
     */
    fun getTokenFlow(): Flow<String?> {
        if (!isSecureStorageAvailable()) {
            return kotlinx.coroutines.flow.flowOf(null)
        }

        return context.dataStore.data.map { preferences ->
            try {
                preferences[tokenKey]?.let { encryptedToken ->
                    decrypt(encryptedToken)
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Проверяет наличие токена без расшифровки
     */
    suspend fun hasToken(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[tokenKey] != null
        }.first()
    }

    /**
     * Удаляет токен из хранилища и ключ из Keystore
     */
    suspend fun clearToken() {
        // Удаляем из DataStore
        context.dataStore.edit { preferences ->
            preferences.remove(tokenKey)
        }

        // Удаляем ключ из Keystore
        if (isSecureStorageAvailable()) {
            try {
                val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
                    load(null)
                }
                if (keyStore.containsAlias(keyAlias)) {
                    keyStore.deleteEntry(keyAlias)
                }
            } catch (e: Exception) {
                // Логируем ошибку, но не бросаем исключение
                e.printStackTrace()
            }
        }
    }

    /**
     * Очищает все данные (для выхода пользователя)
     */
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }

        if (isSecureStorageAvailable()) {
            try {
                val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
                    load(null)
                }
                if (keyStore.containsAlias(keyAlias)) {
                    keyStore.deleteEntry(keyAlias)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
