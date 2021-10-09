package cz.encircled.fswing.components

/**
 * Indicates that the component must be notified before being removed
 */
interface RemovalAware {

    val cancelableListeners: MutableList<Cancelable>

    fun beforeRemoved() {
        cancelableListeners.forEach { it.cancel() }
        cancelableListeners.clear()
    }

    fun Cancelable.cancelOnRemove() {
        cancelableListeners.add(this)
    }

}
