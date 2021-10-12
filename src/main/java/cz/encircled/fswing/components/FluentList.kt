package cz.encircled.fswing.components

import cz.encircled.fswing.observable.collection.ObservableCollection
import cz.encircled.fswing.observable.observableList
import java.util.*
import javax.swing.JList

class FluentList<T>(initial: ObservableCollection<T> = observableList()) : JList<T>(), RemovalAware {

    private lateinit var data: ObservableCollection<T>
    override val cancelableListeners: MutableList<Cancelable> = arrayListOf()

    init {
        rebind(initial)
    }

    fun rebind(new: ObservableCollection<T>) {
        cancelableListeners.forEach { it.cancel() }
        data = new
        onDataChange()
        cancelableListeners.add(data.onChange { _, _ ->
            onDataChange()
        })
    }

    private fun onDataChange() {
        setListData(Vector(data))
    }

}