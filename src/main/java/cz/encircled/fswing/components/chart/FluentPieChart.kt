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
    val chartPanel: ChartPanel = ChartPanel(ChartFactory.createPieChart("", DefaultPieDataset<String>())),
) : FluentPanel(), RemovalAware {

    val minimalThreshold: LongProperty = SimpleLongProperty(-1)
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
        val min = minimalThreshold.get()

        source
            .filter { valueMapper(it).toLong() >= min }
            .forEachIndexed { index, item ->
                dataset.insertValue(index, keyMapper(item), valueMapper(item))
            }

        if (dataset.keys.size < source.size) {
            dataset.insertValue(dataset.keys.size, FluentSwingSettings.ln["Other"], source
                .map { valueMapper(it).toLong() }
                .filter { it < min }
                .sum())
        }

        val chart = ChartFactory.createPieChart("", dataset)
        chart.setTextAntiAlias(true)
        chartPanel.chart = chart
    }

}