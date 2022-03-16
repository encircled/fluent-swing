package cz.encircled.fswing.components.input

import cz.encircled.fswing.components.RemovalAware
import cz.encircled.fswing.x
import javafx.beans.property.ObjectProperty
import java.time.LocalDate

class FluentDateInput(
    placeholder: String? = null,
    val emptyValue: LocalDate = LocalDate.MIN,

    /**
     * OnChange waits for `onChangeWaitTime` ms after last typing (prevents from invocation of OnChange after each type)
     */
    onChangeWaitTime: Long = 300,
) : BaseInput<LocalDate>(placeholder, onChangeWaitTime), RemovalAware {

    private val delimiters: List<String> = listOf(" ", "-", ".")

    init {
        preferredSize = 170 x 30
    }

    fun bind(to: ObjectProperty<LocalDate>): FluentDateInput {
        onChange { to.set(it) }
        return this
    }

    override fun mapValue(value: String): LocalDate {
        val delimiter = delimiters.firstOrNull { value.contains(it) }
        return if (delimiter != null) {
            val tokens = value.split(delimiter)
            LocalDate.of(tokens[1].toInt(), tokens[0].toInt(), 1)
        } else {
            val year = value.toIntOrNull()
            if (year == null) emptyValue else LocalDate.of(year, 1, 1)
        }
    }

}