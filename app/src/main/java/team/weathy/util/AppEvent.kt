package team.weathy.util

import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.MutableSharedFlow

fun SimpleSharedFlow() = MutableSharedFlow<Unit>(1, 0, DROP_OLDEST)
fun MutableSharedFlow<Unit>.emit() = tryEmit(Unit)

object AppEvent {
    val onWeathyUpdated = SimpleSharedFlow()
}