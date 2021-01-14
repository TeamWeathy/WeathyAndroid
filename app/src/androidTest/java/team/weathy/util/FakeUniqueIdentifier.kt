package team.weathy.util

import java.util.*

class FakeUniqueIdentifier : UniqueIdentifier {
    override val id: String?
        get() = FakeUniqueIdentifier.id
    override val userId: Int?
        get() = FakeUniqueIdentifier.userId
    override val userNickname: String
        get() = FakeUniqueIdentifier.userNickname
    override val exist: Boolean
        get() = FakeUniqueIdentifier.exist

    override fun generate(): String {
        return UUID.randomUUID().toString()
    }

    override fun saveId(uuid: String): Boolean {
        FakeUniqueIdentifier.id = uuid
        return true
    }

    override fun saveUserId(userId: Int): Boolean {
        FakeUniqueIdentifier.userId = userId
        return true
    }

    override fun saveUserNickname(nickname: String): Boolean {
        FakeUniqueIdentifier.userNickname = nickname
        return true
    }

    override fun saveToken(token: String) {
        FakeUniqueIdentifier.token = token
    }

    override fun loadToken(): String? {
        return FakeUniqueIdentifier.token
    }

    companion object {
        var token: String? = null
        var id: String? = null
        var userId: Int? = null
        var userNickname = ""
        var exist = false
    }
}