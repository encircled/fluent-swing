package cz.encircled.fswing.components.modal

import cz.encircled.fswing.settings.FluentSwingSettings
import javax.swing.JOptionPane

object OptionPane {

    fun getUserInput(msg: String, initial: Any, onInput: (String) -> Unit) {
        val res = JOptionPane.showInputDialog(
            null,
            FluentSwingSettings.ln[msg],
            initial
        )

        if (res != null) {
            onInput(res)
        }
    }

    fun getUserConfirmation(msg: String, onConfirm: () -> Unit) =
        OptionPane.getUserConfirmation(msg, onConfirm) {}

    fun getUserConfirmation(msg: String, onConfirm: () -> Unit, onDecline: () -> Unit) {
        when (JOptionPane.showConfirmDialog(
            null,
            FluentSwingSettings.ln[msg],
            FluentSwingSettings.ln["Confirmation"],
            JOptionPane.INFORMATION_MESSAGE
        )) {
            0 -> onConfirm.invoke()
            1 -> onDecline.invoke()
        }
    }

}