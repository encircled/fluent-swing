package cz.encircled.fswing.components

import cz.encircled.fswing.onChange
import cz.encircled.fswing.settings.FluentSwingSettings
import cz.encircled.fswing.time.Scheduled
import javafx.beans.property.LongProperty
import javax.swing.JTextField

class FluentNumberInput(
    placeholder: String? = null,
    /**
     * OnChange waits for `onChangeWaitTime` ms after last typing (prevents from invocation of OnChange after each type)
     */
    onChangeWaitTime: Long = 300,
) : JTextField(), RemovalAware {

    override val cancelableListeners: MutableList<Cancelable> = mutableListOf()
    private val onChangeSchedule: Scheduled = Scheduled(onChangeWaitTime)

    init {
        if (placeholder != null) {
            putClientProperty("JTextField.placeholderText", FluentSwingSettings.ln[placeholder])
        }
    }

    fun onChange(callback: (Long?) -> Unit): FluentNumberInput {
        cancelableListeners.add(document.onChange {
            onChangeSchedule.postpone {
                callback(it.toLongOrNull())
            }
        })
        return this
    }

    fun bind(to: LongProperty): FluentNumberInput {
        onChange { to.set(it ?: 0) }
        return this
    }

}