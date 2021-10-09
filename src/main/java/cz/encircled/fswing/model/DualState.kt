package cz.encircled.fswing.model

data class DualState<T>(
    val initial: T,
    val alternative: T = initial
)