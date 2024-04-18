package transportationproblem.optimalMethod

import transportationproblem.ReferenceSolution
import transportationproblem.OptimalSolution
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class OptimalMethod() {
    fun getOptimalSolution(
        referenceSolution: ReferenceSolution
    ): Flow<OptimalSolution> = flow {
        val rates = referenceSolution.rates
        var basicCells = referenceSolution.basicCells
        var f = referenceSolution.f
        var potentials = calculatePotentials(rates, basicCells)
        var grades = calculateGrades(rates, basicCells, potentials)
        var isOptimal = isOptimal(grades)
        emit(OptimalSolution(rates, basicCells, f, isOptimal))
        while (!isOptimal) {
            val minNegativeGrade = getMinNegativeGrade(grades)
            basicCells = redistribute(basicCells, minNegativeGrade)
            f = calculateF(rates, basicCells)
            potentials = calculatePotentials(rates, basicCells)
            grades = calculateGrades(rates, basicCells, potentials)
            isOptimal = isOptimal(grades)
            emit(OptimalSolution(rates, basicCells, f, isOptimal))
        }
    }

    private fun calculatePotentials(
        rates: MutableList<MutableList<Int>>,
        basicCells: MutableList<MutableList<Int>>,
    ): Potentials {
        val countStocks = rates.size
        val countDemands = rates.first().size
        val u: MutableList<Int> = MutableList(countStocks) { Int.MIN_VALUE }
        val v: MutableList<Int> = MutableList(countDemands) { Int.MIN_VALUE }
        u[0] = 0
        var noCalculatedPotentials = countDemands + countStocks - 1
        while (noCalculatedPotentials != 0) {
            for (i in basicCells.indices) {
                for (j in basicCells.first().indices) {
                    if (basicCells[i][j] != -1 && u[i] != Int.MIN_VALUE && v[j] == Int.MIN_VALUE) {
                        v[j] = rates[i][j] - u[i]
                        noCalculatedPotentials--
                    } else if (basicCells[i][j] != -1 && u[i] == Int.MIN_VALUE && v[j] != Int.MIN_VALUE) {
                        u[i] = rates[i][j] - v[j]
                        noCalculatedPotentials--
                    }
                }
            }
        }
        return Potentials(u, v)
    }

    private fun calculateGrades(
        rates: MutableList<MutableList<Int>>,
        basicCells: MutableList<MutableList<Int>>,
        potentials: Potentials
    ): List<Grade> {
        val grades: MutableList<Grade> = mutableListOf()
        val u = potentials.u
        val v = potentials.v
        for (i in basicCells.indices) {
            for (j in basicCells.first().indices) {
                if (basicCells[i][j] == -1) {
                    val value = rates[i][j] - (u[i] + v[j])
                    val grade = Grade(i, j, value)
                    grades.add(grade)
                }
            }
        }
        return grades
    }

    private fun isOptimal(grades: List<Grade>): Boolean {
        val negativeGrades = grades.filter { it.value < 0 }
        return negativeGrades.isEmpty()
    }

    private fun getMinNegativeGrade(grades: List<Grade>): Grade {
        val negativeGrades = grades.sortedBy { it.value }
        return negativeGrades[0]
    }

    private fun redistribute(
        basicCells: MutableList<MutableList<Int>>,
        negativeGrade: Grade
    ): MutableList<MutableList<Int>> {
        val cellsIndices = mutableListOf<Pair<Int, Int>>()
        for (i in basicCells.indices) {
            for (j in basicCells.first().indices) {
                if (basicCells[i][j] != -1) cellsIndices.add(Pair(i, j))
            }
        }
        val posNegativeGrade = Pair(negativeGrade.row, negativeGrade.col)
        val loop = buildLoop(cellsIndices, listOf(posNegativeGrade), posNegativeGrade)
        if (loop.isNotEmpty()) {
            val minOddCell = getMinOddCell(basicCells, loop)
            basicCells[posNegativeGrade.first][posNegativeGrade.second] = 0
            for (i in loop.indices) {
                val row = loop[i].first
                val col = loop[i].second
                if (i % 2 == 0) {
                    basicCells[row][col] = basicCells[row][col] + minOddCell
                } else {
                    basicCells[row][col] = basicCells[row][col] - minOddCell
                }
            }
            var posZeroCell = Pair(-1, -1)
            var countBasicCells = 0
            for (i in basicCells.indices) {
                for (j in basicCells.first().indices) {
                    if (basicCells[i][j] != -1) countBasicCells++
                    if (basicCells[i][j] == 0) posZeroCell = Pair(i, j)
                }
            }
            println(minOddCell)
            if (countBasicCells > basicCells.size + basicCells.first().size - 1) {
                basicCells[posZeroCell.first][posZeroCell.second] = -1
            }
        }
        return basicCells
    }

    private fun getMinOddCell(
        basicCells: MutableList<MutableList<Int>>,
        loop: List<Pair<Int, Int>>
    ): Int {
        val oddCell = mutableListOf<Int>()
        for (i in loop.indices) {
            val row = loop[i].first
            val col = loop[i].second
            if (i % 2 != 0) {
                oddCell.add(basicCells[row][col])
            }
        }
        return oddCell.min()
    }

    private fun buildLoop(
        cellsIndices: List<Pair<Int, Int>>,
        visitedCells: List<Pair<Int, Int>>,
        posNegativeGrade: Pair<Int, Int>
    ): List<Pair<Int, Int>> {
        if (visitedCells.size > 3) {
            val canBeClose = getNextPossibleCells(visitedCells, listOf(posNegativeGrade)).size == 1
            if (canBeClose) return visitedCells
        }
        val notVisitedCells = cellsIndices.filterNot { it in visitedCells }
        val nextPossibleNext = getNextPossibleCells(visitedCells, notVisitedCells)
        for (cell in nextPossibleNext) {
            val newVisitedCells = buildLoop(cellsIndices, visitedCells + cell, posNegativeGrade)
            if (newVisitedCells.isNotEmpty()) return newVisitedCells
        }
        return emptyList()
    }


    private fun getNextPossibleCells(
        visitedCells: List<Pair<Int, Int>>,
        notVisitedCells: List<Pair<Int, Int>>
    ): List<Pair<Int, Int>> {
        val lastCell = visitedCells.last()
        val cellsInRow = notVisitedCells.filter { it.first == lastCell.first }
        val cellsInColumn = notVisitedCells.filter { it.second == lastCell.second }
        if (visitedCells.size < 2) {
            return cellsInColumn + cellsInRow
        }
        val prevCell = visitedCells[visitedCells.size - 2]
        return if (prevCell.first == lastCell.first) {
            cellsInColumn
        } else {
            cellsInRow
        }
    }

    private fun calculateF(
        rates: MutableList<MutableList<Int>>,
        basicCells: MutableList<MutableList<Int>>
    ): Int {
        var f = 0
        for (i in basicCells.indices) {
            for (j in basicCells.first().indices) {
                if (basicCells[i][j] != -1) {
                    f += basicCells[i][j] * rates[i][j]
                }
            }
        }
        return f
    }
}


