package cz.encircled.fswing.components.input

import cz.encircled.fswing.components.Cancelable
import javafx.beans.property.StringProperty

open class FluentInput(
    placeholder: String? = null,

    onChangeWaitTime: Long = 300,
) : BaseInput<String>(placeholder, onChangeWaitTime) {

    override val cancelableListeners: MutableList<Cancelable> = mutableListOf()

    fun bind(to: StringProperty): FluentInput {
        onChange { to.set(it) }
        return this
    }

    override fun mapValue(value: String): String = value

}
