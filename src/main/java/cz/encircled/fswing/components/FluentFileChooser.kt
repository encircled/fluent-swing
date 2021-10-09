package cz.encircled.fswing.components

import java.io.File
import javax.swing.JFileChooser

class FluentFileChooser : JFileChooser() {

    lateinit var callback: (File) -> Unit

    init {

    }

    fun onSelect(callback: (File) -> Unit): FluentFileChooser {
        this.callback = callback
        return this
    }

    fun open() {
        when (showOpenDialog(null)) {
            APPROVE_OPTION -> callback(selectedFile)
        }
    }

}