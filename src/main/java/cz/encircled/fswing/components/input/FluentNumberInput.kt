package cz.encircled.fswing.components.input

import cz.encircled.fswing.components.Cancelable
import cz.encircled.fswing.components.RemovalAware
import javafx.beans.property.LongProperty

class FluentNumberInput(
    placeholder: String? = null,
    val emptyValue: Long = 0,
    /**
     * OnChange waits for `onChangeWaitTime` ms after last typing (prevents from invocation of OnChange after each type)
     */
    onChangeWaitTime: Long = 300,
) : BaseInput<Long>(placeholder, onChangeWaitTime), RemovalAware {

    override val cancelableListeners: MutableList<Cancelable> = mutableListOf()

    fun bind(to: LongProperty): FluentNumberInput {
        onChange { to.set(it) }
        return this
    }

    override fun mapValue(value: String): Long = value.toLongOrNull() ?: emptyValue

}