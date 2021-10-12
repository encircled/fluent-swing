package cz.encircled.fswing

import cz.encircled.fswing.components.Cancelable
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JComponent
import javax.swing.JSlider
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.Document

inline fun JTextField.onChange(crossinline onChange: (String) -> Unit): JTextField {
    document.onChange(onChange)
    return this
}

inline fun JComponent.onHover(crossinline onEnter: () -> Unit, crossinline onLeft: () -> Unit) {
    addMouseListener(object : MouseAdapter() {

        var isInside: AtomicBoolean = AtomicBoolean(false)

        override fun mouseEntered(e: MouseEvent) {
            if (isInside.compareAndSet(false, true)) {
                onEnter.invoke()
            }
        }

        override fun mouseExited(e: MouseEvent) {
            if (e.point.x < 0 || e.point.x >= this@onHover.width || e.point.y < 0 || e.point.y >= this@onHover.height) {
                if (isInside.compareAndSet(true, false)) {
                    onLeft.invoke()
                }
            }
        }
    })
}

inline fun JComponent.onClick(crossinline callback: (e: MouseEvent) -> Unit): JComponent {
    addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
            callback(e)
        }
    })
    return this
}

inline fun JSlider.onChange(crossinline callback: (Int, Boolean) -> Unit) {
    addChangeListener {
        callback.invoke((it.source as JSlider).value, (it.source as JSlider).valueIsAdjusting)
    }
}

inline fun Document.onChange(crossinline callback: (String) -> Unit) {
    addDocumentListener(object : DocumentListener {
        override fun insertUpdate(e: DocumentEvent) = callback.invoke(e.document.getText(0, e.document.length))

        override fun removeUpdate(e: DocumentEvent) = callback.invoke(e.document.getText(0, e.document.length))

        override fun changedUpdate(e: DocumentEvent) = callback.invoke(e.document.getText(0, e.document.length))
    })
}

inline fun <T> ObservableValue<T>.onChange(crossinline listener: (T) -> Unit): Cancelable {
    val listenerToAdd: ChangeListener<T> = ChangeListener { _: ObservableValue<out T>, _: T, newValue: T ->
        inUiThread {
            listener.invoke(newValue)
        }
    }
    addListener(listenerToAdd)
    return Cancelable {
        removeListener(listenerToAdd)
    }
}

inline fun inNormalThread(crossinline runnable: () -> Unit) {
    if (SwingUtilities.isEventDispatchThread()) {
        CompletableFuture.runAsync {
            runnable.invoke()
        }
    } else {
        runnable.invoke()
    }
}

inline fun inUiThread(crossinline runnable: () -> Unit) {
    if (SwingUtilities.isEventDispatchThread()) {
        runnable.invoke()
    } else {
        SwingUtilities.invokeLater {
            runnable.invoke()
        }
    }
}