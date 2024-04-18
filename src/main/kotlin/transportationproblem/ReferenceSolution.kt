package transportationproblem

data class ReferenceSolution(
    val rates: MutableList<MutableList<Int>>,
    val basicCells: MutableList<MutableList<Int>>,
    val f: Int
)
