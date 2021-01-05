package team.weathy.util

import java.util.*

class UniqueIdentifier(private val spUtil: SPUtil) {
    val id
        get() = spUtil.sharedPreferences.getString(KEY, null).let { savedId ->
            debugE(savedId)
            savedId ?: generate().toString().also { id ->
                spUtil.sharedPreferences.edit().putString(KEY, id).commit()
            }
        }

    val exist
        get() = spUtil.sharedPreferences.contains(KEY)

    private fun generate() = UUID.randomUUID()

    companion object {
        private const val KEY = "UNIQUE_ID"
    }
}