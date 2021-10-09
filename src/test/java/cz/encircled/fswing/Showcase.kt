package cz.encircled.fswing

import com.formdev.flatlaf.FlatDarculaLaf
import cz.encircled.fswing.components.FluentPanel
import cz.encircled.fswing.components.table.FluentTable
import cz.encircled.fswing.model.GridData
import cz.encircled.fswing.observable.observableList
import javafx.collections.FXCollections.observableArrayList
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTabbedPane

class Showcase : JFrame() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            FlatDarculaLaf.setup()
            Showcase().isVisible = true
        }
    }

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        preferredSize = 1024 x 768
        size = 1024 x 768

        layout = BorderLayout()
        val tabs = JTabbedPane()
        add(tabs, BorderLayout.CENTER)

        tabs.addTab("Table", tableTab())
        tabs.addTab("Layout", layoutTab())
    }

    private fun tableTab(): FluentPanel {
        val dynEnum = observableArrayList(DynamicEnum("D1"), DynamicEnum("D2"))

        return gridPanel {
            nextRow {
                JScrollPane(
                    FluentTable(
                        TestEntity::class,
                        observableList(
                            TestEntity("name1", 2, SomeEnum.Pizza, dynEnum[0], true),
                            TestEntity("name2", 4, SomeEnum.Burger, dynEnum[1], false),
                        )
                    )
                        .dynamicEnumColumn("dynamic", dynEnum)
                        .withAddItemPopup {
                            TestEntity("", 0, SomeEnum.Noodles, dynEnum[0], true)
                        }
                        .withDeleteItemPopup()
                )
            }
        }
    }

    private fun layoutTab() = gridPanel {
        nextColumn(GridData(widthWeight = 0.2)) {
            JPanel().apply { background = Color.RED }
        }
        nextColumn(GridData()) {
            gridPanel {
                nextRow(GridData(height = 100)) {
                    JPanel().apply { background = Color.BLUE }
                }
                nextRow {
                    JPanel().apply { background = Color.YELLOW }
                }
            }
        }

        nextRow(GridData(heightWeight = 0.1, gridWeight = 2)) {
            JPanel().apply { background = Color.CYAN }
        }
    }

    data class TestEntity(
        var name: String,
        var count: Int,
        var food: SomeEnum,
        var dynamic: DynamicEnum,
        var boolean: Boolean,
    )

    data class DynamicEnum(
        val name: String
    ) {
        override fun toString(): String = name
    }

    enum class SomeEnum {
        Pizza,
        Pasta,
        Burger,
        Noodles,
    }


}