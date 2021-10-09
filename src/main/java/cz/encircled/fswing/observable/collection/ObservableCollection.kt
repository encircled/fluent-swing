package cz.encircled.fswing.observable.collection

import cz.encircled.fswing.components.Cancelable

interface ObservableCollection<T> : MutableList<T> {

    fun onChange(listener: (added: List<T>, removed: List<T>) -> Unit): Cancelable

}