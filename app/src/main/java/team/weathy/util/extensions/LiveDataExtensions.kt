package team.weathy.util.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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

inline fun <T> MutableLiveData<Set<T>>.updateSet(updater: MutableSet<T>.() -> Unit) {
    val draft = value?.toMutableSet()
    draft?.run(updater)
    value = draft
}

fun <T> MediatorLiveData(value: T, block: MediatorLiveData<T>.() -> Unit) = MediatorLiveData<T>().apply {
    this.value = value
    block(this)
}

inline fun <T, A, B> MediatorLiveData<T>.addSources(
    l1: LiveData<A>, l2: LiveData<B>, crossinline callback: (p1: A, p2: B) -> Unit
) {
    addSource(l1) {
        callback(l1.value!!, l2.value!!)
    }
    addSource(l2) {
        callback(l1.value!!, l2.value!!)
    }
}

inline fun <T, A, B, C> MediatorLiveData<T>.addSources(
    l1: LiveData<A>, l2: LiveData<B>, l3: LiveData<C>, crossinline callback: (p1: A, p2: B, p3: C) -> Unit
) {
    addSources(l1, l2) { v1, v2 ->
        callback(v1, v2, l3.value!!)
    }
    addSource(l3) {
        callback(l1.value!!, l2.value!!, l3.value!!)
    }
}

inline fun <T, A, B, C, D> MediatorLiveData<T>.addSources(
    l1: LiveData<A>,
    l2: LiveData<B>,
    l3: LiveData<C>,
    l4: LiveData<D>,
    crossinline callback: (p1: A, p2: B, p3: C, p4: D) -> Unit
) {
    addSources(l1, l2, l3) { v1, v2, v3 ->
        callback(v1, v2, v3, l4.value!!)
    }
    addSource(l4) {
        callback(l1.value!!, l2.value!!, l3.value!!, l4.value!!)
    }
}

inline fun <T, A, B, C, D, E> MediatorLiveData<T>.addSources(
    l1: LiveData<A>,
    l2: LiveData<B>,
    l3: LiveData<C>,
    l4: LiveData<D>,
    l5: LiveData<E>,
    crossinline callback: (p1: A, p2: B, p3: C, p4: D, p5: E) -> Unit
) {
    addSources(l1, l2, l3, l4) { v1, v2, v3, v4 ->
        callback(v1, v2, v3, v4, l5.value!!)
    }
    addSource(l5) {
        callback(l1.value!!, l2.value!!, l3.value!!, l4.value!!, l5.value!!)
    }
}