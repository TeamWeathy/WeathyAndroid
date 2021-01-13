package team.weathy.util

import team.weathy.util.UniqueIdentifier.Companion.KEY
import team.weathy.util.UniqueIdentifier.Companion.TOKEN_KEY
import team.weathy.util.UniqueIdentifier.Companion.USER_ID
import team.weathy.util.UniqueIdentifier.Companion.USER_NICKNAME
import java.util.*
import javax.inject.Inject

interface UniqueIdentifier {
    val id: String?
    val userId: Int?
    val userNickname: String
    val exist: Boolean
    fun generate(): String
    fun saveId(uuid: String): Boolean
    fun saveUserId(userId: Int): Boolean
    fun saveUserNickname(nickname: String): Boolean
    fun saveToken(token: String)
    fun loadToken(): String?

    companion object {
        const val KEY = "UNIQUE_ID"
        const val TOKEN_KEY = "TOKEN"
        const val USER_ID = "USER_ID"
        const val USER_NICKNAME = "USER_NICKNAME"
    }
}

class UniqueIdentifierImpl @Inject constructor(private val spUtil: SPUtil) : UniqueIdentifier {
    override val id: String?
        get() = spUtil.sharedPreferences.getString(KEY, null)

    override val userId: Int
        get() = spUtil.sharedPreferences.getInt(USER_ID, 0)

    override val userNickname: String
        get() = spUtil.sharedPreferences.getString(USER_NICKNAME, "") ?: ""

    override val exist
        get() = spUtil.sharedPreferences.contains(KEY)

    override fun generate() = UUID.randomUUID().toString()

    override fun saveId(uuid: String) = spUtil.sharedPreferences.edit().putString(KEY, uuid).commit()

    override fun saveUserId(userId: Int) = spUtil.sharedPreferences.edit().putInt(USER_ID, userId).commit()

    override fun saveUserNickname(nickname: String) =
        spUtil.sharedPreferences.edit().putString(USER_NICKNAME, userNickname).commit()

    override fun saveToken(token: String) {
        spUtil.sharedPreferences.edit().putString(TOKEN_KEY, token).commit()
    }

    override fun loadToken(): String? {
        return spUtil.sharedPreferences.getString(TOKEN_KEY, null)
    }
}