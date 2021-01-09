package team.weathy.util

import java.util.*

class UniqueIdentifier(private val spUtil: SPUtil) {
    val id: String?
        get() = spUtil.sharedPreferences.getString(KEY, null)

    val exist
        get() = spUtil.sharedPreferences.contains(KEY)

    fun generate() = UUID.randomUUID().toString()

    fun save(uuid: String) = spUtil.sharedPreferences.edit().putString(KEY, uuid.toString()).commit()

    companion object {
        private const val KEY = "UNIQUE_ID"
    }
}