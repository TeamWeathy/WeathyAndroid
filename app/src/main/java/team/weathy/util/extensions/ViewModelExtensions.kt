package team.weathy.util.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <R> ViewModel.launchCatch(
    block: suspend CoroutineScope.() -> R,
    loading: MutableLiveData<Boolean>? = null,
    onSuccess: ((result: R) -> Unit)? = null,
    onFailure: ((e: Throwable) -> Unit)? = null,
    onFinally: (() -> Unit)? = null,
): Job {
    return ApiLauncher.launchCatch(viewModelScope, block, loading, onSuccess, onFailure, onFinally)
}

fun <R> AppCompatActivity.launchCatch(
    block: suspend CoroutineScope.() -> R,
    loading: MutableLiveData<Boolean>? = null,
    onSuccess: ((result: R) -> Unit)? = null,
    onFailure: ((e: Throwable) -> Unit)? = null,
    onFinally: (() -> Unit)? = null,
): Job {
    return ApiLauncher.launchCatch(lifecycleScope, block, loading, onSuccess, onFailure, onFinally)
}

fun <R> Fragment.launchCatch(
    block: suspend CoroutineScope.() -> R,
    loading: MutableLiveData<Boolean>? = null,
    onSuccess: ((result: R) -> Unit)? = null,
    onFailure: ((e: Throwable) -> Unit)? = null,
    onFinally: (() -> Unit)? = null,
): Job {
    return ApiLauncher.launchCatch(lifecycleScope, block, loading, onSuccess, onFailure, onFinally)
}

object ApiLauncher {
    fun <R> launchCatch(
        scope: CoroutineScope,
        block: suspend CoroutineScope.() -> R,
        loading: MutableLiveData<Boolean>? = null,
        onSuccess: ((result: R) -> Unit)? = null,
        onFailure: ((e: Throwable) -> Unit)? = null,
        onFinally: (() -> Unit)? = null,
    ): Job {
        val launchBlock: suspend CoroutineScope.() -> Unit = {
            loading?.value = true
            runCatching<R> {
                withContext(Dispatchers.IO) {
                    block()
                }
            }.onSuccess {
                onSuccess?.invoke(it)
            }.onFailure {
                onFailure?.invoke(it)
            }
            onFinally?.invoke()
            loading?.value = false
        }

        return if (scope is LifecycleCoroutineScope) {
            scope.launchWhenStarted {
                launchBlock()
            }
        } else {
            scope.launch {
                launchBlock()
            }
        }
    }
}

