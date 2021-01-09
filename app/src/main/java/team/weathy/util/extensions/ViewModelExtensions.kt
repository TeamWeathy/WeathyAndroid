package team.weathy.util.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun <R> ViewModel.launchCatch(
    block: suspend CoroutineScope.() -> R,
    loading: MutableLiveData<Boolean>? = null,
    onSuccess: ((result: R) -> Unit)? = null,
    onFailure: ((e: Throwable) -> Unit)? = null,
    onFinally: (() -> Unit)? = null,
) {
    viewModelScope.launch {
        loading?.value = true
        runCatching<R> {
            block()
        }.onSuccess {
            onSuccess?.invoke(it)
        }.onFailure {
            onFailure?.invoke(it)
        }
        onFinally?.invoke()
        loading?.value = false
    }
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
    addSource(l1) {
        callback(l1.value!!, l2.value!!, l3.value!!)
    }
    addSource(l2) {
        callback(l1.value!!, l2.value!!, l3.value!!)
    }
    addSource(l3) {
        callback(l1.value!!, l2.value!!, l3.value!!)
    }
}