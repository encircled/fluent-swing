package cz.encircled.fswing

import cz.encircled.fswing.components.RemovalAware
import cz.encircled.fswing.settings.FluentSwingSettings
import java.awt.Component
import java.awt.Dimension
import javax.swing.ImageIcon
import javax.swing.JComponent


fun JComponent.addAll(vararg components: Component): JComponent {
    components.forEach(this::add)
    revalidate()
    repaint()
    return this
}

fun JComponent.addAll(components: List<Component>) {
    components.forEach(this::add)
    revalidate()
    repaint()
}

inline fun JComponent.removeIf(crossinline callback: (c: Component) -> Boolean) {
    for (component in components) {
        if (callback.invoke(component)) {
            if (component is RemovalAware) {
                try {
                    component.beforeRemoved()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            remove(component)
        }
    }
    revalidate()
    repaint()
}

fun JComponent.removeSelf() {
    val p = parent
    if (this is RemovalAware) {
        beforeRemoved()
    }
    p.remove(this)
    p.validate()
    p.repaint()
}

fun String.toIcon() = try {
    ImageIcon(FluentSwingSettings::class.java.getResource("/icons/$this"))
} catch (e: Exception) {
    println("Icon not found: $this")
    throw e
}

infix fun Int.x(other: Int) = Dimension(this, other)
