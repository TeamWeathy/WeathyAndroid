package team.weathy.util

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
import androidx.security.crypto.MasterKey

class SPUtil(context: Application) {
    private val masterKey: MasterKey? = try {
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    } catch (e: Throwable) {
        null
    }
    val sharedPreferences: SharedPreferences = masterKey?.let {
        EncryptedSharedPreferences.create(
            context, SP_NAME, masterKey, AES256_SIV, AES256_GCM
        )
    } ?: context.getSharedPreferences(SP_NAME, MODE_PRIVATE)

    var isFirstLaunch: Boolean
        get() {
            val result = sharedPreferences.getBoolean("isFirstLaunch", true)
            if (!result) {
                sharedPreferences.edit().putBoolean("isFirstLaunch", false).commit()
            }
            return result
        }
        set(_) {
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).commit()
        }

    companion object {
        private const val SP_NAME = "DO_NOT_CHANGE_THIS"
    }
}