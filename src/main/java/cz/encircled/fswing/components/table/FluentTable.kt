package cz.encircled.fswing.components.table

import cz.encircled.fswing.components.Cancelable
import cz.encircled.fswing.components.FluentComboBox
import cz.encircled.fswing.components.RemovalAware
import cz.encircled.fswing.observable.collection.ObservableCollection
import cz.encircled.fswing.onClick
import cz.encircled.fswing.settings.FluentSwingSettings.ln
import javafx.collections.ObservableList
import javax.swing.*
import javax.swing.table.DefaultTableModel
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField


class FluentTable<T : Any>(
    clazz: KClass<T>,
    data: ObservableCollection<T>,
    private val model: ObservableModel<T> = ObservableModel(clazz, data)
) : JTable(model), RemovalAware {

    private val columnToDynamicEnum: MutableMap<String, ObservableList<*>> = mutableMapOf()
    override val cancelableListeners: MutableList<Cancelable> = arrayListOf()

    init {
        putClientProperty("terminateEditOnFocusLost", true)

        setEditors()

        componentPopupMenu = JPopupMenu()
        tableHeader.componentPopupMenu = componentPopupMenu
        cancelableListeners.add(data.onChange { _, _ ->
            model.fireTableDataChanged()
        })
    }

    private fun setEditors() {
        model.indexToProp.forEach { (index, prop) ->
            columnModel.getColumn(index).cellEditor = buildEditor(prop)
        }
    }

    fun dynamicEnumColumn(column: String, list: ObservableList<*>): FluentTable<T> {
        columnToDynamicEnum[column] = list
        setEditors()
        return this
    }

    /**
     * Add popup menu item for adding a new row
     */
    fun withAddItemPopup(newItem: () -> T): FluentTable<T> {
        componentPopupMenu.add(JMenuItem(ln["Add"]).onClick {
            model.data.add(newItem())
        })
        return this
    }

    /**
     * Add popup menu item for deleting a row
     */
    fun withDeleteItemPopup(): FluentTable<T> {
        componentPopupMenu.add(JMenuItem(ln["Delete"]).onClick {
            model.data.removeAt(selectedRow)
        })
        return this
    }

    private fun buildEditor(prop: KProperty1<*, Any?>): DefaultCellEditor {
        if (columnToDynamicEnum.containsKey(prop.name)) {
            return DefaultCellEditor(FluentComboBox(columnToDynamicEnum.getValue(prop.name)))
        }

        val clazz = prop.javaField!!.type
        return when {
            Enum::class.java.isAssignableFrom(clazz) -> {
                DefaultCellEditor(FluentComboBox(clazz.enumConstants.toList()))
            }
            clazz == Boolean::class.java -> {
                val checkBox = JCheckBox()
                checkBox.horizontalAlignment = JLabel.CENTER
                DefaultCellEditor(checkBox)
            }
            else -> {
                DefaultCellEditor(JTextField())
            }
        }
    }

    class ObservableModel<T : Any>(clazz: KClass<T>, val data: ObservableCollection<T>) : DefaultTableModel() {

        val indexToProp: Map<Int, KProperty1<T, Any?>> = clazz.memberProperties.reversed()
            .mapIndexed { index: Int, p: KProperty1<T, *> -> Pair(index, p) }
            .associateBy({ it.first }, { it.second })

        init {
            indexToProp.keys.forEach {
                addColumn(indexToProp.getValue(it).name)
            }
        }

        override fun getColumnClass(columnIndex: Int): Class<*> {
            val type = indexToProp.getValue(columnIndex).javaField!!.type
            if (type.name == "boolean") {
                return java.lang.Boolean::class.java
            }
            return type
        }

        override fun getRowCount(): Int = data?.size ?: 0

        override fun getValueAt(row: Int, column: Int): Any {
            return indexToProp.getValue(column).get(data[row]) ?: ""
        }

        override fun setValueAt(value: Any?, row: Int, column: Int) {
            val prop = indexToProp.getValue(column)
            val valToSet = convertIfNeeded(value, prop)

            (prop as KMutableProperty1<T, Any?>).set(data[row], valToSet)
        }

        private fun convertIfNeeded(value: Any?, prop: KProperty1<T, Any?>): Any? {
            val clazz = prop.javaField!!.type

            return if (value == null) null
            else when {
                value is FluentComboBox.LocalizedObject<*> -> value.item
                clazz == Int::class.java -> (value as String).toInt()
                else -> value
            }
        }

    }

}