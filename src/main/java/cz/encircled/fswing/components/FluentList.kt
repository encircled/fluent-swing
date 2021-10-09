package cz.encircled.fswing.components

import cz.encircled.fswing.onChange
import javafx.collections.ObservableList
import java.util.*
import javax.swing.JList

class FluentList<T>(data: ObservableList<T>) : JList<T>(), RemovalAware {

    private var data: ObservableList<T>? = null
    override val cancelableListeners: MutableList<Cancelable> = arrayListOf()

    init {
        onDataChange()
        cancelableListeners.add(data.onChange { _, _ ->
            onDataChange()
        })
    }

    private fun onDataChange() {
        setListData(Vector(data ?: listOf()))
    }

}