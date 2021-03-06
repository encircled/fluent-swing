package cz.encircled.fswing

import cz.encircled.fswing.components.FluentPanel
import cz.encircled.fswing.components.FluentToggleButton
import cz.encircled.fswing.components.input.FluentDateInput
import cz.encircled.fswing.layout.WrapLayout
import cz.encircled.fswing.model.DualState
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Container
import java.awt.FlowLayout
import java.time.LocalDate
import javax.swing.BoxLayout
import javax.swing.ButtonGroup


inline fun gridPanel(crossinline init: FluentPanel.() -> Unit): FluentPanel = FluentPanel().apply { this.init() }

inline fun borderPanel(crossinline init: FluentPanel.() -> Unit): FluentPanel =
    FluentPanel(BorderLayout()).apply { this.init() }

inline fun flowPanel(
    hgap: Int = 5,
    vhap: Int = 5,
    align: Int = FlowLayout.LEFT,
    crossinline init: FluentPanel.() -> Unit = {}
): FluentPanel =
    FluentPanel(FlowLayout(align, hgap, vhap)).apply { this.init() }

inline fun wrapPanel(
    hgap: Int = 5,
    vhap: Int = 5,
    align: Int = FlowLayout.LEFT,
    crossinline init: FluentPanel.() -> Unit = {}
): FluentPanel =
    FluentPanel(WrapLayout(align, hgap, vhap)).apply { this.init() }

inline fun boxPanel(
    align: Int = BoxLayout.Y_AXIS,
    crossinline init: FluentPanel.() -> Unit = {}
): FluentPanel =
    FluentPanel().apply {
        this.layout = BoxLayout(this, align)
        this.init()
    }

fun iconButton(
    iconName: String,
    tooltip: String = "",
    group: ButtonGroup? = null,
    onClick: () -> Unit = {}
): Component =
    FluentToggleButton(iconName = DualState(iconName)).apply {
        onClick { onClick() }
        this.toolTipText = tooltip
        group?.add(this)
    }

// SHORTCUTS FOR ADDING FLUENT CLASSES

fun Container.fluentDateInput(
    placeholder: String? = null,
    emptyValue: LocalDate = LocalDate.MIN,

    /**
     * OnChange waits for `onChangeWaitTime` ms after last typing (prevents from invocation of OnChange after each type)
     */
    onChangeWaitTime: Long = 300,
) = addReturn(FluentDateInput(placeholder, emptyValue, onChangeWaitTime))

private fun <T : Component> Container.addReturn(c: T): T {
    add(c)
    return c
}