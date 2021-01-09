package team.weathy.util.extensions

import androidx.lifecycle.MutableLiveData

inline fun <T> MutableLiveData<List<T>>.updateList(updater: MutableList<T>.() -> Unit) {
    val draft = value?.toMutableList() ?: mutableListOf()
    updater(draft)
    value = draft
}