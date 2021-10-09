package cz.encircled.fswing.components

import cz.encircled.fswing.onChange
import cz.encircled.fswing.settings.FluentSwingSettings
import javafx.beans.property.ObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox

class FluentComboBox<T>(values: List<T> = listOf()) : JComboBox<FluentComboBox.LocalizedObject<T>>(),
    RemovalAware {

    private val data: ObservableList<T>
    override val cancelableListeners: MutableList<Cancelable> = arrayListOf()

    init {
        if (values is ObservableList<T>) {
            this.data = values
            this.data.onChange { _, _ ->
                onDataChange()
            }
            onDataChange()
        } else {
            model = DefaultComboBoxModel(values.map { LocalizedObject(it) }.toTypedArray())
            data = FXCollections.observableArrayList()
        }
    }

    fun bind(prop: ObjectProperty<T>): FluentComboBox<T> {
        addItemListener {
            prop.value = (it.item as LocalizedObject<T>).item
        }
        return this
    }

    private fun onDataChange() {
        model = DefaultComboBoxModel(data
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
