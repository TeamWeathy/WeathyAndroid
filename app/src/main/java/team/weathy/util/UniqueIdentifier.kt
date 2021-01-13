package team.weathy.util

import team.weathy.util.UniqueIdentifier.Companion.KEY
import team.weathy.util.UniqueIdentifier.Companion.TOKEN_KEY
import java.util.*
import javax.inject.Inject

interface UniqueIdentifier {
    val id: String?
    val exist: Boolean
    fun generate(): String
    fun save(uuid: String): Boolean
    fun saveToken(token: String)
    fun loadToken(): String?

    companion object {
        const val KEY = "UNIQUE_ID"
        const val TOKEN_KEY = "TOKEN"
    }
}

class UniqueIdentifierImpl @Inject constructor(private val spUtil: SPUtil) : UniqueIdentifier {
    override val id: String?
        get() = spUtil.sharedPreferences.getString(KEY, null)

    override val exist
        get() = spUtil.sharedPreferences.contains(KEY)

    override fun generate() = UUID.randomUUID().toString()

    override fun save(uuid: String) = spUtil.sharedPreferences.edit().putString(KEY, uuid).commit()

    override fun saveToken(token: String) {
        spUtil.sharedPreferences.edit().putString(TOKEN_KEY, token).commit()
    }

    override fun loadToken(): String? {
        return spUtil.sharedPreferences.getString(TOKEN_KEY, null)
    }
}