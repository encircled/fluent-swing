package cz.encircled.fswing.components

import cz.encircled.fswing.model.GridData
import cz.encircled.fswing.settings.FluentSwingSettings
import cz.encircled.fswing.x
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.LayoutManager
import javax.swing.JPanel
import kotlin.math.max

open class FluentPanel(
    defLayout: LayoutManager = GridBagLayout(),
    var anchor: Int = GridBagConstraints.NORTH,
) : JPanel() {

    var currentRow = -1
    var currentColumn = -1

    init {
        layout = defLayout
    }

    inline fun nextRow(gridData: GridData = GridData(), crossinline component: () -> Component): Component {
        currentColumn = 0
        return addNext(component.invoke(), currentColumn, ++currentRow, gridData)
    }

    inline fun nextRow(width: Int? = null, height: Int? = null, crossinline component: () -> Component): Component {
        return nextRow(GridData(width, height), component)
    }

    inline fun nextColumn(gridData: GridData = GridData(), crossinline component: () -> Component): Component {
        currentRow = max(0, currentRow)
        return addNext(component.invoke(), ++currentColumn, currentRow, gridData)
    }

    inline fun nextColumn(width: Int? = null, height: Int? = null, crossinline component: () -> Component): Component {
        return nextColumn(GridData(width, height), component)
    }

    fun addNext(component: Component, x: Int, y: Int, data: GridData): Component {
        val constraints = GridBagConstraints()
        constraints.weightx = 1.0
        constraints.weighty = 1.0

        constraints.fill = if (data.width == null && data.height == null) GridBagConstraints.BOTH
        else if (data.width != null) GridBagConstraints.VERTICAL
        else GridBagConstraints.HORIZONTAL

        if (data.height != null || data.width != null) {
            setDimensionalConstraints(data, constraints, component)
        } else {
            constraints.weightx = data.widthWeight ?: 1.0
            constraints.weighty = data.heightWeight ?: 1.0
        }

        if (data.fill != null) {
            constraints.fill = data.fill
        }

        constraints.gridx = x
        constraints.gridy = y
        constraints.gridheight = data.gridHeight
        constraints.gridwidth = data.gridWeight

        constraints.anchor = anchor

        if (layout is GridBagLayout) {
            add(component, constraints)
        } else {
            add(component)
        }

        return component
    }

    private fun setDimensionalConstraints(
        data: GridData,
        constraints: GridBagConstraints,
        component: Component
    ) {
        if (data.height != null && data.width != null) {
            constraints.weighty = 0.0
            constraints.weightx = 0.0
            component.minimumSize = data.width x data.height
            component.preferredSize = data.width x data.height
            return
        }
        if (data.height != null) {
            constraints.weighty = 0.0
            component.minimumSize = FluentSwingSettings.minHeight x data.height
            component.preferredSize = FluentSwingSettings.minWidth x data.height
        }
        if (data.width != null) {
            constraints.weightx = 0.0
            component.minimumSize = width x component.minimumSize.height
            component.preferredSize = width x component.preferredSize.height
        }
    }

}