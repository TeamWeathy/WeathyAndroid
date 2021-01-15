package team.weathy.util

import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalDate

fun SimpleSharedFlow() = MutableSharedFlow<Unit>(1, 0, DROP_OLDEST)
fun MutableSharedFlow<Unit>.emit() = tryEmit(Unit)

object AppEvent {
    val onWeathyUpdated = SimpleSharedFlow()
    val onNavigateCurWeathyInCalendar = MutableSharedFlow<LocalDate>(1, 0, DROP_OLDEST)
}