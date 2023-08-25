package ru.uniyar.ac.utils

import ru.uniyar.ac.entities.FractionType
import ru.uniyar.ac.entities.PivotData
import ru.uniyar.ac.entities.TaskType
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.floor

/*fun simplexTableToList(simplexTable: MutableMap<Int, MutableMap<Int, Double>>): List<List<String>> {
    var result = emptyList<List<String>>()
    var existingVariableNumbers = emptySet<Int>()

    for (row in simplexTable) {
        if (row.key != 0) {
            var firstRow = listOf<String>()
            firstRow = firstRow.plus("X^")
            for (elementNumber in row.value.keys) {
                firstRow = firstRow.plus("X${elementNumber}")
                existingVariableNumbers = existingVariableNumbers.plus(elementNumber)
            }
            result = result.plusElement(firstRow)
            break
        }
    }

    for (row in simplexTable) {
        if (row.key != 0) {
            var listRow = listOf<String>()
            listRow = listRow.plus("X${row.key}")
            for (elementValue in row.value.values)
                listRow = listRow.plus(elementValue.toString())
            result = result.plusElement(listRow)
        }
    }

    var lastRow = listOf<String>()
    lastRow = lastRow.plus("")
    for (element in simplexTable.getOrDefault(0, emptyMap())) {
        if (element.key in existingVariableNumbers)
            lastRow = lastRow.plus(element.value.toString())
    }
    result = result.plusElement(lastRow)

    return result
}*/

fun elementsFromSimplexTable(simplexTable: MutableMap<Int, MutableMap<Int, Double>>, pivots: List<Pair<Int, Int>>, fractionType: FractionType): List<List<Pair<String, PivotData>>> {
    var result = emptyList<List<Pair<String, PivotData>>>()
    var existingVariableNumbers = emptySet<Int>()

    for (row in simplexTable)
        if (row.key != 0) {
            for (elementNumber in row.value.keys)
                existingVariableNumbers = existingVariableNumbers.plus(elementNumber)
            break
        }

    for (row in simplexTable)
        if (row.key != 0) {
            val listRow = mutableListOf<Pair<String, PivotData>>()
            for (element in row.value)
                listRow.add(
                    if (isPivot(row.key, element.key, pivots))
                        Pair(element.value.toString(fractionType), PivotData(row.key, element.key))
                    else
                        Pair(element.value.toString(fractionType), PivotData(-1, -1))
                )
            Collections.rotate(listRow, -1)
            result = result.plusElement(listRow.toList())
        }

    val lastRow = mutableListOf<Pair<String, PivotData>>()
    for (element in simplexTable.getOrDefault(0, emptyMap())) {
        if (element.key in existingVariableNumbers)
            lastRow.add(
                if (isPivot(0, element.key, pivots))
                    Pair(element.value.toString(fractionType), PivotData(0, element.key))
                else
                    Pair(element.value.toString(fractionType), PivotData(-1, -1))
            )
    }
    Collections.rotate(lastRow, -1)
    result = result.plusElement(lastRow.toList())

    return result
}

fun columnNamesFromSimplexTable(simplexTable: MutableMap<Int, MutableMap<Int, Double>>): List<String> {
    var result = emptyList<String>()

    for (row in simplexTable)
        if (row.key != 0) {
            for (elementNumber in row.value.keys)
                if (elementNumber != 0)
                    result = result.plus("X$elementNumber")
            break
        }

    return result.plusElement("C")
}

fun rowNamesFromSimplexTable(simplexTable: MutableMap<Int, MutableMap<Int, Double>>): List<String> {
    var result = emptyList<String>()

    for (row in simplexTable)
        if (row.key != 0)
            result = result.plus("X${row.key}")

    return result
}

fun transpose(matrix: List<List<Pair<String, PivotData>>>): List<List<Pair<String, PivotData>>> {
    val columnCount = matrix.first().size
    val rowCount = matrix.size

    val transpose = MutableList(columnCount) { MutableList(rowCount) { Pair("", PivotData(-1, -1)) } }
    for (i in 0 until rowCount)
        for (j in 0 until columnCount)
            transpose[j][i] = matrix[i][j]

    return transpose.map { it.toList() }.toList()
}

fun Double.toString(fractionType: FractionType): String =
    when (fractionType) {
        FractionType.DECIMAL -> {
            val df = DecimalFormat("#.##")
            df.format(this).toString()
        }
        FractionType.COMMON -> doubleToFraction(this, 1.0E-5, 2147483647, maxIterations = 100)
    }

private fun doubleToFraction(value: Double, epsilon: Double, maxDenominator: Int, maxIterations: Int): String {
    if (value == 0.0)
        return "0"

    val overflow = 2147483647L
    var r0 = value
    var a0 = floor(value).toLong()
    val numerator: Int
    val denominator: Int

    if (abs(a0) > overflow) {
        throw NumberFormatException()
    } else if (abs(a0.toDouble() - value) < epsilon) {
        numerator = a0.toInt()
        denominator = 1
    } else {
        var p0 = 1L
        var q0 = 0L
        var p1 = a0
        var q1 = 1L
        var p2: Long
        var q2: Long
        var n = 0
        var stop = false
        while (true) {
            ++n
            val r1 = 1.0 / (r0 - a0.toDouble())
            val a1 = floor(r1).toLong()
            p2 = a1 * p1 + p0
            q2 = a1 * q1 + q0
            if (abs(p2) <= overflow && abs(q2) <= overflow) {
                val convergent = p2.toDouble() / q2.toDouble()
                if (n < maxIterations && abs(convergent - value) > epsilon && q2 < maxDenominator.toLong()) {
                    p0 = p1
                    p1 = p2
                    q0 = q1
                    q1 = q2
                    a0 = a1
                    r0 = r1
                } else {
                    stop = true
                }
                if (!stop) {
                    continue
                }
                break
            }
            if (epsilon != 0.0 || abs(q1) >= maxDenominator.toLong()) {
                throw NumberFormatException()
            }
            break
        }
        if (n >= maxIterations) {
            throw NumberFormatException()
        } else {
            if (q2 < maxDenominator.toLong()) {
                numerator = p2.toInt()
                denominator = q2.toInt()
            } else {
                numerator = p1.toInt()
                denominator = q1.toInt()
            }
        }
    }

    return "${numerator}${if ((denominator == 1) || (denominator == 0)) "" else "/$denominator"}"
}

fun String.parseFraction(): Double =
    if (this.contains("/")) {
        val fractionParts = this.split("/")
        fractionParts[0].toDouble() / fractionParts[1].toDouble()
    } else
        this.toDouble()

fun isPivot(row: Int, column: Int, pivots: List<Pair<Int, Int>>): Boolean {
    for (pivot in pivots)
        if ((pivot.first == row) and (pivot.second == column))
            return true
    return false
}

fun MutableList<Double>.inverseFirstIfMax(taskType: TaskType): MutableList<Double> {
    val first = this.firstOrNull()
    if ((first != null) and (taskType == TaskType.MAX))
        this[0] = this[0] * -1
    return this
}
