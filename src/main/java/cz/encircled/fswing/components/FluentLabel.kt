package cz.encircled.fswing.components

import cz.encircled.fswing.onChange
import cz.encircled.fswing.settings.FluentSwingSettings
import javafx.beans.property.ObjectProperty
import java.awt.Font
import javax.swing.JLabel

open class FluentLabel(
    text: String = "",
    isBold: Boolean = false,
    fontSize: Float = FluentSwingSettings.fontSize,
) : JLabel(text), RemovalAware {

    override val cancelableListeners: MutableList<Cancelable> = mutableListOf()

    init {
        val style = if (isBold) font.style or Font.BOLD else font.style
        font = font.deriveFont(style, fontSize)
    }

    fun <T : Any> bind(to: ObjectProperty<T>): FluentLabel {
        text = to.value.toString()
        cancelableListeners.add(to.onChange {
            text = it.toString()
        })
        return this
    }

}