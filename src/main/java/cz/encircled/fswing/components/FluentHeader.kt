package cz.encircled.fswing.components

import cz.encircled.fswing.settings.FluentSwingSettings

open class FluentHeader(
    text: String = "",
    isBold: Boolean = true,
    fontSize: Float = FluentSwingSettings.headerFontSize,
) : FluentLabel(text, isBold, fontSize)
