package cz.encircled.fswing.components

import cz.encircled.fswing.model.DualState
import cz.encircled.fswing.toIcon
import javax.swing.JToggleButton

class FluentToggleButton(
    text: String? = null,
    iconName: DualState<String>? = null
) : JToggleButton() {

    init {
        isBorderPainted = false
        isFocusPainted = false
        isContentAreaFilled = false

        setText(text)

        if (iconName != null) {
            icon = iconName.initial.toIcon()
            rolloverIcon = iconName.initial.replace(".", "_hover.").toIcon()

            selectedIcon = iconName.alternative.toIcon()
            rolloverSelectedIcon = iconName.alternative.replace(".", "_hover.").toIcon()
        }
    }

}