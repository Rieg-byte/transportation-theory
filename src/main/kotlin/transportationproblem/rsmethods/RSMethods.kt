package transportationproblem.rsmethods

import transportationproblem.ReferenceSolution
import kotlin.math.min

interface RSMethod {
    fun getReferenceSolution(
        demands: MutableList<Int>,
        stocks: MutableList<Int>,
        rates: MutableList<MutableList<Int>>,
    ): ReferenceSolution
}

class RSMethodNWC(): RSMethod {
    override fun getReferenceSolution(
        demands: MutableList<Int>,
        stocks: MutableList<Int>,
        rates: MutableList<MutableList<Int>>,
    ): ReferenceSolution {
        val cells = MutableList(stocks.size) { MutableList(demands.size) { -1 } }
        var i = 0
        var j = 0
        var f = 0
        while (i < stocks.size && j < demands.size) {
            val minSupplyDemand = min(stocks[i], demands[j])
            cells[i][j] = minSupplyDemand
            f += rates[i][j] * minSupplyDemand
            demands[j] -= minSupplyDemand
            stocks[i] -= minSupplyDemand
            if (stocks[i] == 0) i++
            if (demands[j] == 0) j++
        }
        return ReferenceSolution(rates, cells, f)
    }
}

