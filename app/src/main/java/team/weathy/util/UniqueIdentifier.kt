package team.weathy.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import team.weathy.util.UniqueIdentifier.Companion.KEY
import team.weathy.util.UniqueIdentifier.Companion.TOKEN_KEY
import team.weathy.util.UniqueIdentifier.Companion.USER_ID
import team.weathy.util.UniqueIdentifier.Companion.USER_NICKNAME
import java.util.*
import javax.inject.Inject

interface UniqueIdentifier {
    val id: String?
    val userId: Int?
    val userNickname: StateFlow<String>
    val exist: Boolean
    fun generate(): String
    fun saveId(uuid: String): Boolean
    fun saveUserId(userId: Int): Boolean
    fun saveUserNickname(nickname: String)
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
    // FIXME 시연용
    override val id: String?
        get() = TEST_ID
    //        get() = spUtil.sharedPreferences.getString(KEY, null)

    // FIXME 시연용
    override val userId: Int
        get() = TEST_USER_ID
    //        get() = spUtil.sharedPreferences.getInt(USER_ID, 0)

    override val userNickname = MutableStateFlow("")

    override val exist
        get() = spUtil.sharedPreferences.contains(KEY)

    override fun generate() = UUID.randomUUID().toString()

    override fun saveId(uuid: String) = spUtil.sharedPreferences.edit().putString(KEY, uuid).commit()

    override fun saveUserId(userId: Int) = spUtil.sharedPreferences.edit().putInt(USER_ID, userId).commit()

    override fun saveUserNickname(nickname: String) {
        spUtil.sharedPreferences.edit().putString(USER_NICKNAME, nickname).commit()
        userNickname.value = nickname
    }

    override fun saveToken(token: String) {
        spUtil.sharedPreferences.edit().putString(TOKEN_KEY, token).commit()
    }

    // FIXME 시연용
    override fun loadToken(): String? {
        return spUtil.sharedPreferences.getString(TOKEN_KEY, null) ?: "token"
    }

    init {
        userNickname.value = spUtil.sharedPreferences.getString(USER_NICKNAME, "웨디") ?: "웨디"
    }

    companion object {
        // FIXME 시연용
        private const val TEST_ID = "010-8966-1467"
        private const val TEST_USER_ID = 50
    }
}