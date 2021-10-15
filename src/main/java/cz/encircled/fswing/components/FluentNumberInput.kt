package cz.encircled.fswing.components

import javafx.beans.property.LongProperty

class FluentNumberInput(
    placeholder: String? = null,
    /**
     * OnChange waits for `onChangeWaitTime` ms after last typing (prevents from invocation of OnChange after each type)
     */
    onChangeWaitTime: Long = 300,
) : BaseInput<Long>(placeholder, onChangeWaitTime), RemovalAware {

    override val cancelableListeners: MutableList<Cancelable> = mutableListOf()

    fun bind(to: LongProperty): FluentNumberInput {
        onChange { to.set(it ?: 0) }
        return this
    }

    override fun mapValue(value: String): Long? = value.toLongOrNull()

}