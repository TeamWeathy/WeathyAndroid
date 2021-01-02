package team.weathy.util

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.reflect.KProperty

class OnChangeProp<T>(private var field: T, private val callback: (value: T) -> Unit) {
    operator fun setValue(thisRef: Any?, p: KProperty<*>, v: T) {
        field = v
        callback(v)
    }

    operator fun getValue(thisRef: Any?, p: KProperty<*>): T {
        return field
    }
}

class OnLiveDataProp<T>(initialValue: T, liveDataCallback: (liveData: LiveData<T>) -> Unit) {
    private val liveData = MutableLiveData(initialValue)

    init {
        liveDataCallback(liveData)
    }

    operator fun setValue(thisRef: Any?, p: KProperty<*>, v: T) {
        liveData.value = v
    }

    operator fun getValue(thisRef: Any?, p: KProperty<*>): T {
        return liveData.value!!
    }
}

class OnValidateProp<T>(private var field: T) {
    operator fun setValue(thisRef: View?, p: KProperty<*>, v: T) {
        field = v
        thisRef?.invalidate()
    }

    operator fun getValue(thisRef: View?, p: KProperty<*>): T {
        return field
    }
}

class OnLayoutProp<T>(private var field: T) {
    operator fun setValue(thisRef: View?, p: KProperty<*>, v: T) {
        field = v
        thisRef?.requestLayout()
    }

    operator fun getValue(thisRef: View?, p: KProperty<*>): T {
        return field
    }
}