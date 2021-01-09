package team.weathy.util.extensions

import androidx.lifecycle.MutableLiveData

inline fun <T> MutableLiveData<List<T>>.updateList(updater: MutableList<T>.() -> Unit) {
    val draft = value?.toMutableList()
    draft?.run(updater)
    value = draft
}

inline fun <K, V> MutableLiveData<Map<K, V>>.updateMap(updater: MutableMap<K, V>.() -> Unit) {
    val draft = value?.toMutableMap()
    draft?.run(updater)
    value = draft
}