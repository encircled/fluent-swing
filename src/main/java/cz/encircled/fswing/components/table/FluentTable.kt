package cz.encircled.fswing.components.table

import cz.encircled.fswing.components.Cancelable
import cz.encircled.fswing.components.FluentComboBox
import cz.encircled.fswing.components.RemovalAware
import cz.encircled.fswing.observable.collection.ObservableCollection
import cz.encircled.fswing.onClick
import cz.encircled.fswing.settings.FluentSwingSettings.ln
import java.awt.Component
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

    private val columnToDynamicEnum: MutableMap<String, ObservableCollection<*>> = mutableMapOf()
    override val cancelableListeners: MutableList<Cancelable> = arrayListOf()

    init {
        putClientProperty("terminateEditOnFocusLost", true)

        setCellEditors()

        showHorizontalLines = true
        autoCreateRowSorter = true

        onClick {
            val row = rowAtPoint(it.point)
            if (row > -1) {
                setRowSelectionInterval(row, row)
                if (columnSelectionAllowed) {
                    val column = columnAtPoint(it.point)
                    if (column > -1) setColumnSelectionInterval(column, column)
                }
            }
        }

        componentPopupMenu = JPopupMenu()
        tableHeader.componentPopupMenu = componentPopupMenu
        cancelableListeners.add(data.onChange { _, _ ->
            model.fireTableDataChanged()
        })
    }

    fun readOnly(): FluentTable<T> {
        model.isReadOnly = true
        return this
    }

    fun noHorizontalLines(): FluentTable<T> {
        setShowHorizontalLines(false)
        return this
    }

    fun verticalLines(): FluentTable<T> {
        setShowVerticalLines(true)
        return this
    }

    fun cellSelectable(): FluentTable<T> {
        columnSelectionAllowed = true
        return this
    }

    fun selectedColumnName(): String {
        val c = selectedColumn
        return if (c == -1) "" else columnModel.getColumn(c).headerValue.toString()
    }

    fun selectedItem(): T? {
        val row = selectedRow
        return if (row > -1) {
            val realRow = rowSorter.convertRowIndexToModel(row)
            model.data[realRow]
        } else null
    }

    fun dynamicEnumColumn(column: String, list: ObservableCollection<*>): FluentTable<T> {
        columnToDynamicEnum[column] = list
        setCellEditors()
        return this
    }

    /**
     * Add popup menu item for adding a new row
     */
    fun withAddItemPopup(newItem: () -> T): FluentTable<T> {
        componentPopupMenu.add(JMenuItem(ln["Add"]).onClick {
            model.data.add(newItem())
            val i = rowSorter.convertRowIndexToView(model.data.size - 1)
            selectionModel.setSelectionInterval(i, i)
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

    private fun setCellEditors() {
        model.indexToProp.forEach { (index, prop) ->
            columnModel.getColumn(index).cellEditor = getCellEditorForColumn(prop)
        }
    }

    private fun getCellEditorForColumn(prop: KProperty1<*, Any?>): DefaultCellEditor {
        if (columnToDynamicEnum.containsKey(prop.name)) {
            val comboBox = FluentComboBox(columnToDynamicEnum.getValue(prop.name))
            return object : DefaultCellEditor(comboBox) {
                override fun getTableCellEditorComponent(
                    table: JTable?,
                    value: Any?,
                    isSelected: Boolean,
                    row: Int,
                    column: Int
                ): Component {
                    // Preserve currently set value
                    val fluentComboBox = editorComponent as FluentComboBox<Any>
                    fluentComboBox.selectedIndex = fluentComboBox.data.indexOf(value!!)
                    return super.getTableCellEditorComponent(table, value, isSelected, row, column)
                }
            }
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

        var isReadOnly = false

        val indexToProp: Map<Int, KProperty1<T, Any?>> = clazz.memberProperties.reversed()
            .mapIndexed { index: Int, p: KProperty1<T, *> -> Pair(index, p) }
            .associateBy({ it.first }, { it.second })

        init {
            indexToProp.keys.forEach {
                addColumn(indexToProp.getValue(it).name)
            }
        }

        override fun isCellEditable(row: Int, column: Int): Boolean {
            return !isReadOnly && super.isCellEditable(row, column)
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