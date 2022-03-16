package cz.encircled.fswing

import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.border.EmptyBorder

fun JComponent.padding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    border = EmptyBorder(top, left, bottom, right)
}

fun JComponent.border(color: Color, thickness: Int = 1) {
    border = BorderFactory.createLineBorder(color, thickness)
}

fun JComponent.border(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0, color: Color) {
    border = BorderFactory.createMatteBorder(top, left, bottom, right, color)
}

fun JComponent.scrollable(increment: Int = 30): JScrollPane {
    val scroll = JScrollPane(this)
    scroll.verticalScrollBar.unitIncrement = increment
    scroll.horizontalScrollBar.unitIncrement = increment
    return scroll
}
