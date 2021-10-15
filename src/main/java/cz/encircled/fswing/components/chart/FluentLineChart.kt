package cz.encircled.fswing.components.chart

import cz.encircled.fswing.components.Cancelable
import cz.encircled.fswing.components.FluentPanel
import cz.encircled.fswing.components.RemovalAware
import cz.encircled.fswing.observable.collection.ObservableCollection
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.general.DefaultPieDataset

/**
 * Requires jfreechart
 */
open class FluentLineChart<S>(
    val source: ObservableCollection<S>,
    val rowMapper: (S) -> Comparable<*>,
    val columnMapper: (S) -> Comparable<*>,
    val valueMapper: (S) -> Number,
    val xLabel: String = "X",
    val yLabel: String = "Y",
    val title: String = "",
) : FluentPanel(), RemovalAware {

    private val chartPanel: ChartPanel = ChartPanel(ChartFactory.createPieChart("", DefaultPieDataset<String>()))
    override val cancelableListeners: MutableList<Cancelable> = mutableListOf()

    init {
        nextRow { chartPanel }

        applyDataToChart()
        setListeners()
    }

    private fun setListeners() {
        cancelableListeners.add(source.onChange { _, _ -> applyDataToChart() })
    }

    private fun applyDataToChart() {
        val dataset = DefaultCategoryDataset()

        source
            .forEach { item ->
                dataset.addValue(valueMapper(item), rowMapper(item), columnMapper(item))
            }

        val chart = ChartFactory.createLineChart(title, xLabel, yLabel, dataset)
        chart.setTextAntiAlias(true)
        chartPanel.chart = chart
    }

}