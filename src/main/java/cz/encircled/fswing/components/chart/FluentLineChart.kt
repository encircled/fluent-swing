package cz.encircled.fswing.components.chart

import cz.encircled.fswing.components.Cancelable
import cz.encircled.fswing.components.FluentPanel
import cz.encircled.fswing.components.RemovalAware
import cz.encircled.fswing.observable.collection.ObservableCollection
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.axis.DateAxis
import org.jfree.chart.axis.DateTickUnit
import org.jfree.chart.axis.DateTickUnitType
import org.jfree.chart.labels.XYToolTipGenerator
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.general.DefaultPieDataset
import org.jfree.data.time.RegularTimePeriod
import org.jfree.data.time.TimeSeries
import org.jfree.data.time.TimeSeriesCollection
import org.jfree.data.time.Year
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Requires jfreechart
 */
open class FluentLineChart<S>(
    val source: ObservableCollection<S>,
    val rowMapper: (S) -> Comparable<*>,
    val columnMapper: (S) -> RegularTimePeriod,
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
        val dataset = TimeSeriesCollection()
        source.groupBy { rowMapper(it) }.forEach { (key, data) ->
            val series = TimeSeries(key)
            data.forEach { d ->
                series.add(columnMapper(d), valueMapper(d))
            }
            dataset.addSeries(series)
        }


        val chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset)
        val dateAxis = DateAxis()
        val dateFormat = getDateFormat(dataset)
        dateAxis.dateFormatOverride = dateFormat.first
        dateAxis.tickUnit = dateFormat.second
        val plot = chart.plot as XYPlot
        plot.domainAxis = dateAxis
        chart.setTextAntiAlias(true)

        val r = (plot.renderer as XYLineAndShapeRenderer)
        r.defaultShapesVisible = true
        r.defaultShapesFilled = true

        r.defaultToolTipGenerator =
            XYToolTipGenerator { data, series, item ->
                val key = data.getSeriesKey(series).toString()
                val y = NumberFormat.getCurrencyInstance().format(data.getY(series, item))
                val x = dateFormat.first.format(Date((data.getX(series, item) as Long)))

                "$key: $x, $y"
            }

        chartPanel.chart = chart
        chartPanel.isDomainZoomable = true
        chartPanel.isRangeZoomable = true

        chartPanel.initialDelay = 0
        chartPanel.dismissDelay = Int.MAX_VALUE

        chartPanel.verticalAxisTrace = true
    }

    private fun getDateFormat(dataset: TimeSeriesCollection): Pair<SimpleDateFormat, DateTickUnit> {
        return if (dataset.series.isEmpty() || (dataset.series[0] as TimeSeries).getDataItem(0).period is Year)
            SimpleDateFormat("yyyy") to DateTickUnit(DateTickUnitType.YEAR, 1)
        else SimpleDateFormat("M-yy") to DateTickUnit(DateTickUnitType.MONTH, 1)

    }

}