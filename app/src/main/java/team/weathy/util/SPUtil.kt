package team.weathy.util

import android.app.Application
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
import androidx.security.crypto.MasterKey

class SPUtil(context: Application) {
    private val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    val sharedPreferences = EncryptedSharedPreferences.create(
        context, SP_NAME, masterKey, AES256_SIV, AES256_GCM
    )

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