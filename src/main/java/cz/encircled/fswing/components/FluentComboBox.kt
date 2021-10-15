package cz.encircled.fswing.components

import cz.encircled.fswing.observable.collection.ObservableCollection
import cz.encircled.fswing.observable.observableList
import cz.encircled.fswing.settings.FluentSwingSettings
import javafx.beans.property.ObjectProperty
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox

class FluentComboBox<T>(values: List<T> = listOf()) : JComboBox<FluentComboBox.LocalizedObject<T>>(),
    RemovalAware {

    val selected: T
        get() = (selectedItem as LocalizedObject<T>).item

    val data: ObservableCollection<T>
    override val cancelableListeners: MutableList<Cancelable> = arrayListOf()

    init {
        if (values is ObservableCollection<T>) {
            this.data = values
            this.data.onChange { _, _ ->
                onDataChange()
            }
            onDataChange()
        } else {
            model = DefaultComboBoxModel(values.map { LocalizedObject(it) }.toTypedArray())
            data = observableList()
        }
    }

    fun onChange(callback: (T) -> Unit): FluentComboBox<T> {
        addActionListener {
            callback(selected)
        }
        return this
    }

    // TODO BiDirectional?
    fun bind(prop: ObjectProperty<T>): FluentComboBox<T> {
        addItemListener {
            prop.value = selected
        }
        return this
    }

    private fun onDataChange() {
        model = DefaultComboBoxModel(
            data
                .map { LocalizedObject(it) }
            .sortedBy { it.toString() }
            .toTypedArray()
        )

        if (model.size > 0) selectedIndex = 0
    }

    class LocalizedObject<E>(val item: E) {
        override fun toString(): String = FluentSwingSettings.ln.localize(item.toString())
    }

}
