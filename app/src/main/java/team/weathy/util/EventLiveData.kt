package team.weathy.util

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.*

typealias SimpleEventLiveData = EventLiveData<Unit>

class EventLiveData<T> : LiveData<T>() {
    private val pending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        }
    }

    @MainThread
    fun emit(value: T) {
        pending.set(true)
        setValue(value)
    }
}

fun SimpleEventLiveData.emit() {
    emit(Unit)
}