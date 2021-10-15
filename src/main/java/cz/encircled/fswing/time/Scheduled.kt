package cz.encircled.fswing.time

import java.util.*
import kotlin.concurrent.schedule

class Scheduled(val delay: Long, callback: (() -> Unit)? = null) {

    var timer: TimerTask? = null

    init {
        if (callback != null) schedule(delay, callback)
    }

    inline fun schedule(delay: Long, crossinline callback: () -> Unit) {
        timer?.cancel()
        timer = Timer().schedule(delay) {
            callback()
        }
    }

    inline fun postpone(crossinline callback: () -> Unit): Scheduled {
        schedule(delay, callback)
        return this
    }

    fun cancel(): Scheduled {
        timer?.cancel()
        return this
    }

}