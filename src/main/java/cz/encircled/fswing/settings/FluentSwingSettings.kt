package cz.encircled.fswing.settings

object FluentSwingSettings {

    var minWidth: Int = 800
    var minHeight: Int = 600

    var fontSize = 12f
    var headerFontSize = 16f

    var ln: FluentSwingLocalization = object : FluentSwingLocalization {
        override fun localize(code: String) = code
    }

}