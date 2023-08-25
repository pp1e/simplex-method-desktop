package ru.uniyar.ac.calculations

import ru.uniyar.ac.entities.GaussTable
import ru.uniyar.ac.entities.GaussType

object GaussSolver {
    private var swapedCols: MutableList<Int> = mutableListOf()
    private var type: GaussType = GaussType.ONE
    private var basisList: List<Int> = emptyList()

    fun gauss(basisList: List<Int>, matrix: MutableList<MutableList<Double>>): GaussTable {
        var basis = basisList
        type = GaussType.ONE
        val gaussResult = GaussTable()
        basis = basis.sorted()
        GaussSolver.basisList = ArrayList(basis)
        var gaussMatrix: MutableList<MutableList<Double>> = mutableListOf()
        swapedCols = (1..(matrix[0].size - 1)).toMutableList()
        for (i in matrix.indices)
            gaussMatrix.add(matrix[i].map { it }.toMutableList())
        swapByOneMatr(basis, gaussMatrix)
        gauss(gaussMatrix)
        gaussMatrix = removeZeroRows(gaussMatrix)
        gaussResult.matrix = gaussMatrix
        gaussResult.swappedColumns = swapedCols
        gaussResult.type = type
        return gaussResult
    }

    private fun removeZeroRows(matrix: MutableList<MutableList<Double>>): MutableList<MutableList<Double>> {
        val zeroRows = getZeroRows(matrix)
        val cleanedMatrix: MutableList<MutableList<Double>> = mutableListOf()
        var rowIdx = 0
        for (i in matrix.indices)
            if (!zeroRows.contains(i)) {
                cleanedMatrix.add(matrix[i].map { it }.toMutableList())
                rowIdx++
            }
        return cleanedMatrix
    }

    private fun getZeroRows(matr: MutableList<MutableList<Double>>): List<Int> {
        val rows: MutableList<Int> = mutableListOf()
        for (i in matr.indices)
            if (isZeroRow(matr[i]))
                rows.add(i)
        return rows
    }

    private fun isZeroRow(fractions: List<Double>): Boolean = fractions.all { it.equals(0.0) }

    private fun gauss(matrix: MutableList<MutableList<Double>>) {
        for (i in matrix.indices) {
            replaceNotZero(matrix, i)
            if (matrix[i][i + 1] == 0.0)
                continue
            for (j in i + 1 until matrix.size) {
                addRow(matrix, j, i, matrix[j][i + 1] / matrix[i][i + 1] * -1)
                if (isZeroType(matrix)) {
                    type = GaussType.ZERO
                    return
                }
            }
            rowMultiply(matrix, i, 1 / matrix[i][i + 1])
        }

        for (i in matrix.indices.reversed()) {
            if (matrix[i][i + 1] == 0.0)
                continue
            for (k in i - 1 downTo 0) {
                addRow(matrix, k, i, matrix[k][i + 1] / matrix[i][i + 1] * -1)
                if (isZeroType(matrix)) {
                    type = GaussType.ZERO
                    return
                }
            }
        }
        if (isInfTypeCol(matrix))
            type = GaussType.INFINUM
    }

    private fun isZeroType(matrix: MutableList<MutableList<Double>>): Boolean {
        for (i in matrix.indices)
            if (isZeroTypeRow(matrix, i))
                return true
        return false
    }

    private fun isInfTypeCol(matrix: MutableList<MutableList<Double>>): Boolean {
        for (fractions in matrix)
            for (k in basisList.size + 1 until matrix[0].size)
                if (fractions[k] != 0.0)
                    return true

        for (i in matrix.indices)
            if (matrix[i][i + 1] == 0.0)
                return true

        return false
    }

    private fun isZeroTypeRow(matrix: MutableList<MutableList<Double>>, row: Int): Boolean {
        if (matrix[row][0] == 0.0)
            return false
        for (k in 1 until matrix[row].size)
            if (matrix[row][k] != 0.0)
                return false
        return true
    }

    private fun addRow(matrix: MutableList<MutableList<Double>>, from: Int, to: Int, multiply: Double) {
        for (j in matrix[from].indices)
            matrix[from][j] = matrix[from][j] + (matrix[to][j] * multiply)
    }

    private fun rowMultiply(matrix: MutableList<MutableList<Double>>, row: Int, coefficient: Double) {
        for (i in matrix[row].indices)
            matrix[row][i] = matrix[row][i] * coefficient
    }

    private fun replaceNotZero(matrix: MutableList<MutableList<Double>>, j: Int) {
        var row = j
        for (i in j until matrix.size)
            if (matrix[i][j + 1] != 0.0) {
                row = i
                break
            }
        if (row != j)
            swapRow(matrix, row, j)
    }

    private fun swapByOneMatr(columns: List<Int>, matrix: MutableList<MutableList<Double>>) {
        var currentColumn = 1
        for (value in columns) {
            if (currentColumn == value) {
                currentColumn++
                continue
            }
            swapCol(matrix, currentColumn, value)
            currentColumn++
        }
    }

    private fun swapRow(matrix: MutableList<MutableList<Double>>, a: Int, b: Int) {
        for (i in matrix[0].indices)
            swap(matrix, a, i, b, i)
    }

    private fun swapCol(matrix: MutableList<MutableList<Double>>, a: Int, b: Int) {
        for (i in matrix.indices)
            swap(matrix, i, a, i, b)

        swapedCols[a - 1] = swapedCols[b - 1].also { swapedCols[b - 1] = swapedCols[a - 1] }

        // Collections.swap(swapedCols, a - 1, b - 1)
    }

    fun swapMatrixCol(matrix: MutableList<MutableList<Double>>, a: Int, b: Int) {
        for (i in matrix.indices)
            swap(matrix, i, a, i, b)
    }

    fun swap(matrix: MutableList<MutableList<Double>>, i: Int, a: Int, j: Int, b: Int) {
        matrix[i][a] = matrix[j][b].also { matrix[j][b] = matrix[i][a] }
    }
}
