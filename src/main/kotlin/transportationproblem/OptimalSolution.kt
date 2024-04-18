package transportationproblem

private const val CELL_LENGTH = 8

data class OptimalSolution(
    val rates: MutableList<MutableList<Int>>,
    val basicCells: MutableList<MutableList<Int>>,
    val f: Int,
    val isOptimal: Boolean
) {
    override fun toString(): String = buildString {
        append("Транспортная таблица\n")
        for (i in rates.indices) {
            for (j in rates.first().indices) {
                append("${rates[i][j]}/${if (basicCells[i][j] == -1) "-" else basicCells[i][j]}".padEnd(8))
            }
            append("\n")
        }
        append("F = $f\t")
        if (isOptimal) append("План оптимален") else append("План не оптимален. Есть отрицательные Cij")
    }
}
