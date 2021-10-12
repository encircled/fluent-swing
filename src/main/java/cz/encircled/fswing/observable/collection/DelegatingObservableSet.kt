package cz.encircled.fswing.observable.collection

/**
 * Observable version of Set-like collection
 */
open class DelegatingObservableSet<T>(initial: Set<T> = setOf()) : DelegatingObservableList<T>(initial),
    ObservableSet<T> {

    override fun add(element: T): Boolean {
        val contains = contains(element)
        if (!contains) {
            super.add(element)
        }
        return !contains
    }

    override fun add(index: Int, element: T) {
        val contains = contains(element)
        if (!contains) {
            super.add(index, element)
        }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        var i = index
        elements.forEach {
            if (!contains(it)) add(i++, it)
        }
        return i > index
    }

    override fun addAll(elements: Collection<T>): Boolean {
        var added = false
        elements.forEach {
            if (!contains(it)) {
                add(it)
                added = true
            }
        }
        return added
    }

    override fun setAll(vararg elements: T): Boolean {
        return super.setAll(elements.toSet())
    }

    override fun setAll(elements: Collection<T>): Boolean {
        return super.setAll(elements.toSet())
    }

}