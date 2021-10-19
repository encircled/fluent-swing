package cz.encircled.fswing.components

import cz.encircled.fswing.observable.collection.ObservableCollection
import cz.encircled.fswing.observable.observableList
import java.util.*
import javax.swing.JList

class FluentList<T>(source: ObservableCollection<T> = observableList()) : JList<T>(), RemovalAware {

    private lateinit var data: ObservableCollection<T>
    override val cancelableListeners: MutableList<Cancelable> = arrayListOf()

    init {
        dataSource(source)
    }

    fun dataSource(new: ObservableCollection<T>) {
        cancelableListeners.forEach { it.cancel() }
        data = new
        onDataChange()
        cancelableListeners.add(data.onChange { _, _ ->
            onDataChange()
        })
    }

    fun bind(to: ObservableCollection<T>) {
        onChange {
            to.setAll(it)
        }
    }

    fun onChange(callback: (List<T>) -> Unit): FluentList<T> {
        addListSelectionListener {
            if (!it.valueIsAdjusting) {
                callback(selectedIndices.map { data[it] })
            }
        }

        return this
    }

    private fun onDataChange() {
        setListData(Vector(data))
    }

}