package cz.encircled.fswing.components.chart

import cz.encircled.fswing.components.Cancelable
import cz.encircled.fswing.components.FluentPanel
import cz.encircled.fswing.components.RemovalAware
import cz.encircled.fswing.observable.collection.ObservableCollection
import cz.encircled.fswing.onChange
import cz.encircled.fswing.settings.FluentSwingSettings
import javafx.beans.property.LongProperty
import javafx.beans.property.SimpleLongProperty
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.data.general.DefaultPieDataset

/**
 * Requires jfreechart
 */
open class FluentPieChart<S>(
    val source: ObservableCollection<S>,
    val keyMapper: (S) -> String,
    val valueMapper: (S) -> Number,
) : FluentPanel(), RemovalAware {

    val minimalThreshold: LongProperty = SimpleLongProperty(-1)
    private val chartPanel: ChartPanel = ChartPanel(ChartFactory.createPieChart("", DefaultPieDataset<String>()))
    override val cancelableListeners: MutableList<Cancelable> = mutableListOf()

    init {
        nextRow { chartPanel }

        applyDataToChart()
        setListeners()
    }

    private fun setListeners() {
        cancelableListeners.add(source.onChange { _, _ -> applyDataToChart() })
        cancelableListeners.add(minimalThreshold.onChange { applyDataToChart() })
    }

    private fun applyDataToChart() {
        val dataset = DefaultPieDataset<String>()

        val filtered = filtered()
        filtered.first
            .forEachIndexed { index, item ->
                dataset.insertValue(index, keyMapper(item), valueMapper(item))
            }

        if (filtered.second.isNotEmpty()) {
            val keys = filtered.second.map(keyMapper).toSet().take(10).joinToString(", ")
            dataset.insertValue(dataset.keys.size, FluentSwingSettings.ln["Other"] + " ($keys)",
                filtered.second.sumOf { valueMapper(it).toDouble() })
        }

        val chart = ChartFactory.createPieChart("", dataset)
        chart.setTextAntiAlias(true)
        chartPanel.chart = chart
    }

    private fun filtered(): Pair<List<S>, List<S>> {
        val withFilter = source.groupBy { valueMapper(it).toDouble() >= minimalThreshold.get() }.toMutableMap()
        val passed = withFilter[true]?.sortedByDescending { valueMapper(it).toDouble() } ?: listOf()
        val skipped = withFilter[false] ?: listOf()

        return if (passed.size > 21) {
            (passed.subList(0, 20)) to (passed.subList(20, passed.size) + skipped)
        } else {
            passed to skipped
        }
    }

}