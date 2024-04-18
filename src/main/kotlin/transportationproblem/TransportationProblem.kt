package transportationproblem
import kotlinx.coroutines.flow.Flow
import transportationproblem.optimalMethod.OptimalMethod
import transportationproblem.rsmethods.RSMethod
import transportationproblem.rsmethods.RSMethodNWC


class TransportationProblem(
    val demands: MutableList<Int>,
    val stocks: MutableList<Int>,
    val rates: MutableList<MutableList<Int>>,
    val rsMethod: RSMethod = RSMethodNWC(),
    val optimalMethod: OptimalMethod = OptimalMethod()
) {
    private val sumDemands =  demands.sum()
    private val sumStocks = stocks.sum()

    private fun isClosedType(): Boolean = sumDemands == sumStocks

    private fun checkType() {
        if (!isClosedType()) {
            if (sumStocks > sumDemands) {
                demands.add(sumStocks - sumDemands)
                rates.forEach { it.add(0) }
            } else {
                stocks.add(sumDemands - sumStocks)
                rates.add(MutableList(stocks.size) {0})
            }
        }
    }

    fun solve(): Flow<OptimalSolution> {
        checkType()
        val referenceSolution = rsMethod.getReferenceSolution(demands, stocks, rates)
        return optimalMethod.getOptimalSolution(referenceSolution)
    }
}
