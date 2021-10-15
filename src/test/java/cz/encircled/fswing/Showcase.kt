package cz.encircled.fswing

import com.formdev.flatlaf.FlatDarculaLaf
import cz.encircled.fswing.components.*
import cz.encircled.fswing.components.chart.FluentLineChart
import cz.encircled.fswing.components.chart.FluentPieChart
import cz.encircled.fswing.components.modal.OptionPane.getUserConfirmation
import cz.encircled.fswing.components.modal.OptionPane.getUserInput
import cz.encircled.fswing.components.table.FluentTable
import cz.encircled.fswing.model.GridData
import cz.encircled.fswing.observable.observableList
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
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

        add(
            gridPanel {
                nextRow {
                    val tabs = JTabbedPane()
                    tabs.addTab("Table", tableTab())
                    tabs.addTab("Layout", layoutTab())
                    tabs.addTab("Inputs", inputsTab())
                    tabs.addTab("Charts", chartsTab())
                    tabs
                }
            },
            BorderLayout.CENTER
        )
    }

    private fun chartsTab(): Component {
        val source = observableList(
            TestEntity("E1", 3, SomeEnum.Pasta, DynamicEnum("D1")),
            TestEntity("E2", 2, SomeEnum.Pasta, DynamicEnum("D2")),
            TestEntity("E3", 2, SomeEnum.Pasta, DynamicEnum("D3")),

            TestEntity("E4", 4, SomeEnum.Burger, DynamicEnum("D2")),
            TestEntity("E5", 5, SomeEnum.Burger, DynamicEnum("D3")),
        )

        return gridPanel {
            nextRow(height = 30) {
                FluentLabel("Pie Chart")
            }
            nextRow(height = 300) {
                FluentPieChart(observableList(
                    TestEntity("E1", count = 3),
                    TestEntity("E3", food = SomeEnum.Pasta),
                ), { it.food.name }, { it.count })
            }

            nextRow(height = 30) {
                FluentLabel("Line Chart")
            }
            nextRow(height = 300) {
                FluentLineChart(
                    source,
                    { it.food.name },
                    { it.dynamic.name },
                    { it.count },

                    "Food",
                    "Count",
                    "Line Chart"
                )
            }
        }
    }

    private fun inputsTab(): FluentPanel {
        return gridPanel {
            nextColumn(height = 40) {
                FluentInput("Placeholder...").onChange {
                    println("FluentInput onChange triggered with [$it] value")
                }.onChange {
                    println("FluentInput onChange2 triggered with [$it] value")
                }
            }
            nextColumn(height = 40) {
                FluentNumberInput("Some numbers please").onChange {
                    println("FluentNumberInput onChange triggered with [$it] value")
                }
            }

            nextRow(height = 40) {
                FluentToggleButton("Get user input").onClick {
                    getUserInput("Input, please", "initial value") {
                        println(it)
                    }
                }
            }
            nextRow(height = 40) {
                FluentToggleButton("Confirm, please?").onClick {
                    getUserConfirmation("Yes?") {
                        println("It is confirmed!")
                    }
                }
            }
        }
    }

    private fun tableTab(): FluentPanel {
        val dynEnum = observableList(DynamicEnum("D1"), DynamicEnum("D2"))

        return gridPanel {
            nextRow {
                val table = FluentTable(
                    TestEntity::class,
                    observableList(
                        TestEntity("name1", 2, SomeEnum.Pizza, dynEnum[0], true),
                        TestEntity("name2", 4, SomeEnum.Burger, dynEnum[1], false),
                    )
                )
                JScrollPane(
                    table
                        .dynamicEnumColumn("dynamic", dynEnum)
                        .withAddItemPopup {
                            TestEntity("", 0, SomeEnum.Noodles, dynEnum[0], true)
                        }
                        .withDeleteItemPopup()
                        .onClick {
                            println(table.selectedColumnName())
                        }
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
        var name: String = "TestEntity1",
        var count: Int = 1,
        var food: SomeEnum = SomeEnum.Burger,
        var dynamic: DynamicEnum = DynamicEnum("D1"),
        var boolean: Boolean = true,
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