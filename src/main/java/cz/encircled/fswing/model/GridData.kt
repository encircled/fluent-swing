package cz.encircled.fswing.model

data class GridData(
    val width: Int? = null,
    val height: Int? = null,

    val widthWeight: Double? = null,
    val heightWeight: Double? = null,

    val gridWeight: Int = 1,
    val gridHeight: Int = 1,

    val fill: Int? = null
)