package cz.encircled.fswing.components

import cz.encircled.fswing.onChange
import cz.encircled.fswing.settings.FluentSwingSettings
import cz.encircled.fswing.time.Scheduled
import javax.swing.JTextField

abstract class BaseInput<T>(
    placeholder: String?,
    /**
     * OnChange waits for `onChangeWaitTime` ms after last typing (prevents from invocation of OnChange after each type)
     */
    val onChangeWaitTime: Long = 300,
) : JTextField(), RemovalAware {

    override val cancelableListeners: MutableList<Cancelable> = mutableListOf()
    private val onChangeSchedule: MutableMap<Int, Scheduled> = mutableMapOf()

    @Volatile
    private var callbackIndex = 0

    init {
        if (placeholder != null) {
            putClientProperty("JTextField.placeholderText", FluentSwingSettings.ln[placeholder])
        }
    }

    fun onChange(callback: (T?) -> Unit): BaseInput<T> {
        val index = callbackIndex++
        cancelableListeners.add(document.onChange {
            onChangeSchedule[index]?.cancel()
            onChangeSchedule[index] = Scheduled(onChangeWaitTime) {
                callback(mapValue(it))
            }
        })
        return this
    }

    protected abstract fun mapValue(value: String): T?

}