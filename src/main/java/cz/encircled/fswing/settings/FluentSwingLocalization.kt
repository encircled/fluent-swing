package cz.encircled.fswing.settings

interface FluentSwingLocalization {

    fun localize(code: String): String

    operator fun get(code: String) = localize(code)

}