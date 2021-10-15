package cz.encircled.fswing.observable.collection

import cz.encircled.fswing.observable.observableList

open class FilteredObservableCollection<T>(
    val source: ObservableCollection<T>,
    val idToFilter: MutableMap<String, (T) -> Boolean> = mutableMapOf(),
    val filtered: ObservableCollection<T> = observableList(),
) : ObservableCollection<T> by filtered {

    init {
        applyFilter()
        source.onChange { _, _ -> applyFilter() }
    }

    fun addFilter(name: String, filter: (T) -> Boolean) {
        idToFilter[name] = filter
        applyFilter()
    }

    private fun applyFilter() {
        filtered.setAll(source.filter { candidate ->
            idToFilter.values.all { it(candidate) }
        })
    }

}