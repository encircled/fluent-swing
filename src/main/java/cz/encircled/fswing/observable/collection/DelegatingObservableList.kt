package cz.encircled.fswing.observable.collection

import cz.encircled.fswing.components.Cancelable
import cz.encircled.fswing.inUiThread
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

open class DelegatingObservableList<T>(initial: Collection<T> = listOf()) : ObservableCollection<T> {

    protected val delegate: ObservableList<T> = FXCollections.observableArrayList(initial)

    override fun setAll(vararg elements: T) = delegate.setAll(*elements)

    override fun setAll(elements: Collection<T>) = delegate.setAll(elements)

    override val size: Int
        get() = delegate.size

    override fun contains(element: T): Boolean = delegate.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = delegate.containsAll(elements)

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun onChange(listener: (added: List<T>, removed: List<T>) -> Unit): Cancelable {
        val listenerToAdd = ListChangeListener<T> {
            val added = ArrayList<T>()
            val removed = ArrayList<T>()

            while (it.next()) {
                added.addAll(it.addedSubList)
                removed.addAll(it.removed)
            }

            inUiThread {
                listener.invoke(added, removed)
            }
        }
        delegate.addListener(listenerToAdd)
        return Cancelable {
            delegate.removeListener(listenerToAdd)
        }
    }

    override fun bindIncoming(other: ObservableCollection<T>) {
        other.onChange { added, removed ->
            addAll(added)
            removeAll(removed)
        }
        addAll(other)
    }

    override fun <R : Comparable<R>> sorted(selector: (T) -> R?): ObservableCollection<T> {
        delegate.setAll(delegate.sortedBy { selector(it) })
        return this
    }

    override fun get(index: Int): T = delegate.get(index)

    override fun indexOf(element: T): Int = delegate.indexOf(element)

    override fun iterator(): MutableIterator<T> = delegate.iterator()

    override fun lastIndexOf(element: T): Int = delegate.lastIndexOf(element)

    override fun add(element: T): Boolean = delegate.add(element)

    override fun add(index: Int, element: T) = delegate.add(index, element)

    override fun addAll(index: Int, elements: Collection<T>): Boolean = delegate.addAll(index, elements)

    override fun addAll(elements: Collection<T>): Boolean = delegate.addAll(elements)

    override fun clear() = delegate.clear()

    override fun listIterator(): MutableListIterator<T> = delegate.listIterator()

    override fun listIterator(index: Int): MutableListIterator<T> = delegate.listIterator(index)

    override fun remove(element: T): Boolean = delegate.remove(element)

    override fun removeAll(elements: Collection<T>): Boolean = delegate.removeAll(elements)

    override fun removeAt(index: Int): T = delegate.removeAt(index)

    override fun retainAll(elements: Collection<T>): Boolean = delegate.retainAll(elements)

    override fun set(index: Int, element: T): T = delegate.set(index, element)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = delegate.subList(fromIndex, toIndex)

    override fun equals(other: Any?): Boolean = delegate == other

    override fun hashCode(): Int = delegate.hashCode()

    override fun toString(): String = delegate.toString()

}