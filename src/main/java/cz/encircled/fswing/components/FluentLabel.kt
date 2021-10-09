package cz.encircled.fswing.components

import cz.encircled.fswing.settings.FluentSwingSettings
import java.awt.Font
import javax.swing.JLabel

open class FluentLabel(
    text: String = "",
    isBold: Boolean = false,
    fontSize: Float = FluentSwingSettings.fontSize,
) : JLabel(text) {

    init {
        val style = if (isBold) font.style or Font.BOLD else font.style
        font = font.deriveFont(style, fontSize)
    }

}