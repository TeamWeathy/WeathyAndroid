package team.weathy.util

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import team.weathy.util.SPUtil.Companion.SP_NAME
import javax.inject.Inject

interface SPUtil {
    val sharedPreferences: SharedPreferences
    var isFirstLaunch: Boolean
    var isOtherPlaceSelected: Boolean
    var lastSelectedLocationCode: Long

    companion object {
        const val SP_NAME = "DO_NOT_CHANGE_THIS"
    }
}

class SPUtilImpl @Inject constructor(context: Application) : SPUtil {
    //    private val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    //
    //    override val sharedPreferences: SharedPreferences = try {
    //        EncryptedSharedPreferences.create(
    //            context, SP_NAME, masterKey, AES256_SIV, AES256_GCM
    //        )
    //    } catch (e: Throwable) {
    //        context.getSharedPreferences(SP_NAME, MODE_PRIVATE)
    //    }
    override val sharedPreferences: SharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE)

    override var isFirstLaunch: Boolean
        get() {
            val result = sharedPreferences.getBoolean("isFirstLaunch", true)
            if (result) {
                sharedPreferences.edit().putBoolean("isFirstLaunch", false).commit()
            }
            return result
        }
        set(_) {
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).commit()
        }

    override var isOtherPlaceSelected: Boolean
        get() = sharedPreferences.getBoolean("isOtherPlaceSelected", true)
        set(value) {
            sharedPreferences.edit().putBoolean("isOtherPlaceSelected", value).commit()
        }

    override var lastSelectedLocationCode: Long
        get() = sharedPreferences.getLong("lastSelectedLocationCode", 0L)
        set(value) {
            sharedPreferences.edit().putLong("lastSelectedLocationCode", value).commit()
        }

}