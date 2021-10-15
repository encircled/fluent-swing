package cz.encircled.fswing.observable.collection

import cz.encircled.fswing.components.Cancelable

interface ObservableCollection<T> : MutableList<T> {

    fun onChange(listener: (added: List<T>, removed: List<T>) -> Unit): Cancelable

    fun setAll(elements: Collection<T>): Boolean
    fun setAll(vararg elements: T): Boolean

    fun bindIncoming(other: ObservableCollection<T>)

}