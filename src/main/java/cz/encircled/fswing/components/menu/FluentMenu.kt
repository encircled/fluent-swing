package cz.encircled.fswing.components.menu

import cz.encircled.fswing.components.Cancelable
import cz.encircled.fswing.components.RemovalAware
import cz.encircled.fswing.observable.collection.ObservableCollection
import cz.encircled.fswing.onClick
import cz.encircled.fswing.settings.FluentSwingSettings.ln
import javax.swing.JMenu
import javax.swing.JMenuItem

open class FluentMenu<T>(text: String, val items: List<T>, var callback: (T) -> Unit = {}) : JMenu(ln[text]),
    RemovalAware {

    override val cancelableListeners: MutableList<Cancelable> = mutableListOf()

    init {
        if (items is ObservableCollection<T>) {
            cancelableListeners.add(
                items.onChange { _, _ -> addMenuItems() }
            )
        }
        addMenuItems()
    }

    fun onClick(callback: (T) -> Unit) {
        this.callback = callback
    }

    fun addMenuItems() {
        removeAll()
        items.sortedBy { ln[it.toString()] }.forEach { item ->
            add(JMenuItem(ln[item.toString()]).onClick {
                callback(item)
            })
        }
    }

}