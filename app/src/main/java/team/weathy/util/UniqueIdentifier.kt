package team.weathy.util

import team.weathy.util.UniqueIdentifier.Companion.KEY
import java.util.*
import javax.inject.Inject

interface UniqueIdentifier {
    val id: String?
    val exist: Boolean
    fun generate(): String
    fun save(uuid: String): Boolean

    companion object {
        const val KEY = "UNIQUE_ID"
    }
}

class UniqueIdentifierImpl @Inject constructor(private val spUtil: SPUtil) : UniqueIdentifier {
    override val id: String?
        get() = spUtil.sharedPreferences.getString(KEY, null)

    override val exist
        get() = spUtil.sharedPreferences.contains(KEY)

    override fun generate() = UUID.randomUUID().toString()

    override fun save(uuid: String) = spUtil.sharedPreferences.edit().putString(KEY, uuid.toString()).commit()
}