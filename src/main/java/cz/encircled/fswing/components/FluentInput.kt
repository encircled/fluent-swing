package cz.encircled.fswing.components

import cz.encircled.fswing.onChange
import cz.encircled.fswing.settings.FluentSwingSettings
import cz.encircled.fswing.time.Scheduled
import javafx.beans.property.StringProperty
import javax.swing.JTextField

open class FluentInput(
    text: String = "",
    placeholder: String? = null,

    /**
     * OnChange waits for `onChangeWaitTime` ms after last typing (prevents from invocation of OnChange after each type)
     */
    onChangeWaitTime: Long = 300,
) : JTextField(text), RemovalAware {

    override val cancelableListeners: MutableList<Cancelable> = mutableListOf()
    private val onChangeSchedule: Scheduled = Scheduled(onChangeWaitTime)

    init {
        if (placeholder != null) {
            putClientProperty("JTextField.placeholderText", FluentSwingSettings.ln[placeholder])
        }
    }

    // FIXME TODO PLEASE: this will not work with multiple listeners
    fun onChange(callback: (String) -> Unit): FluentInput {
        cancelableListeners.add(document.onChange {
            onChangeSchedule.postpone {
                callback(it)
            }
        })
        return this
    }


    fun bind(to: StringProperty): FluentInput {
        onChange { to.set(it) }
        return this
    }

}
