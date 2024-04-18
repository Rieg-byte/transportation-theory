import kotlinx.coroutines.runBlocking
import transportationproblem.TransportationProblem

fun main(args: Array<String>) = runBlocking {
    val transportationProblem = TransportationProblem(
        demands = mutableListOf(150, 80, 80, 70, 60, 70, 200),
        stocks = mutableListOf(200, 260, 340),
        rates = mutableListOf(
            mutableListOf(2, 2, 5, 3, 1, 4, 4),
            mutableListOf(3, 2, 1, 2, 3, 2, 3),
            mutableListOf(5, 2, 0, 2, 4, 4, 2),
        )
    )
    solveTransportationProblem(transportationProblem)
}

suspend fun solveTransportationProblem(
    transportationProblem: TransportationProblem
) {
    transportationProblem.solve().collect{
        println(it)
    }
}