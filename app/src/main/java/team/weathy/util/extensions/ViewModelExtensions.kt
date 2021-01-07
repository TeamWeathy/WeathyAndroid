package team.weathy.util.extensions

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
        loading?.value = false
    }
}