package ru.uniyar.ac.calculations

import ru.uniyar.ac.entities.GaussTable
import java.util.concurrent.atomic.AtomicBoolean

class SimplexSolver {
    var table: MutableMap<Int, MutableMap<Int, Double>> = mutableMapOf()
    var answer: MutableList<Double>? = null
    var isSolved = false
        private set
    var iteration = -1
    private var pivot: Pair<Int, Int> = Pair(0, 0)
    var prev: SimplexSolver? = null
    var noSolution = false
    var target: MutableList<Double> = mutableListOf()
    var varCount = 0
    var basisList: MutableList<Int> = mutableListOf()
        get() = ArrayList(field)
        set(basisList) {
            basisList.removeIf { it == 0 }
            basisList.sort()
            field = basisList
        }
    var freeList: MutableList<Int> = mutableListOf()
        get() = ArrayList(field)
        set(freeList) {
            freeList.removeIf { it == 0 }
            freeList.sort()
            field = freeList
        }
    var restrictCount = 0

    constructor()
    constructor(
        iteration: Int,
        pivot: Pair<Int, Int>,
        prev: SimplexSolver?
    ) {
        this.iteration = iteration
        setPivot(pivot)
        this.prev = prev
    }

    operator fun get(basis: Int, free: Int): Double? {
        return if (
            !table.containsKey(basis) ||
            !table[basis]!!.containsKey(free)
        )
            null
        else
            table[basis]!![free]
    }

    fun init(
        target: MutableList<Double>,
        restrict: MutableList<MutableList<Double>>,
        varCount: Int,
        restrictCount: Int,
        basisList: List<Int>
    ) {
        this.target = target
        this.varCount = varCount
        this.restrictCount = restrictCount
        println(basisList)
        println(restrict)
        val gaussObject: GaussTable = GaussSolver.gauss(
            basisList,
            restrict
        )
        val oneMatrix: MutableList<MutableList<Double>> = gaussObject.matrix
        val swappedColumns: List<Int> = gaussObject.swappedColumns
        this.restrictCount = gaussObject.matrix.size
        this.basisList = swappedColumns.subList(0, oneMatrix.size).toMutableList()
        freeList = swappedColumns.filter { !this.basisList.contains(it) }.toMutableList()
        val targetFunc: MutableList<Double> = calculateTarget(oneMatrix, swappedColumns)
        this.target = targetFunc

        for (i in oneMatrix.indices) {
            val freeIterator = freeList.iterator()
            for (j in oneMatrix[i].indices) {
                val rowNum = this.basisList[i]
                if (j == 0)
                    add(rowNum, 0, oneMatrix[i][0])
                else {
                    if (freeIterator.hasNext()) {
                        val colNum = freeIterator.next()
                        var swapIdx = 0
                        for (swapCol in swappedColumns) {
                            if (swapCol == colNum)
                                break
                            swapIdx++
                        }
                        add(rowNum, colNum, oneMatrix[i][swapIdx + 1])
                    }
                }
            }
        }
        for (i in targetFunc.indices)
            if (i == 0)
                add(0, 0, targetFunc[0] * - 1)
            else
                add(0, i, targetFunc[i])
        if (checkSolved()) {
            setAnswer()
            setSolved()
        }
        incIteration()
    }

    private fun mainRestrict(matrix: MutableList<MutableList<Double>>, swapedCols: List<Int>): MutableList<MutableList<Double>> {
        val restrict: MutableList<MutableList<Double>> = mutableListOf()
        val swapedList: MutableList<Int> = ArrayList(swapedCols)
        for (i in matrix.indices)
            restrict.add(matrix[i].map { it }.toMutableList())
        for (colIdx in swapedList.indices) {
            val col = swapedList[colIdx]
            val swapedCol = colIdx + 1
            var swapedIdx = 0
            for (i in swapedList.indices)
                if (swapedList[i] == swapedCol) {
                    swapedIdx = i + 1
                    break
                }
            if (colIdx + 1 != col) {
                GaussSolver.swapMatrixCol(restrict, swapedCol, swapedIdx)
                swapedList[colIdx] = swapedCol
                swapedList[swapedIdx - 1] = col
            }
        }
        return restrict
    }

    private fun checkSolved(): Boolean {
        val colMap: Map<Int, Double> = table.getOrDefault(0, mutableMapOf())
        val hasSolution = colMap.entries
            .filter { it.key != 0 }
            .map { it.value }
            .all { it.compareTo(0) >= 0 }
        val hasNegativeB = hasNegativeB()
        if (hasNoSolutionNegativeB()) {
            noSolution = true
            return true
        }
        if (hasNegativeB) {
            noSolution = false
            return false
        }
        val noSolution = AtomicBoolean(false)
        colMap.entries
            .filter { it.key != 0 }
            .forEach { (col, func): Map.Entry<Int, Double> ->
                if (func.compareTo(0.0) < 0) {
                    val no = table.entries
                        .all { it.value.getOrDefault(col, 0.0).compareTo(0.0) <= 0 }
                    if (no)
                        noSolution.set(true)
                }
            }
        this.noSolution = noSolution.get()
        return hasSolution || noSolution.get()
    }

    private fun hasNoSolutionNegativeB(): Boolean {
        if (!hasNegativeB())
            return false
        var minRow = basisList[0]
        var minValue: Double? = get(minRow, 0)
        for (i in basisList) {
            val value: Double? = get(i, 0)
            if (
                value!!.compareTo(0.0) < 0 &&
                value.compareTo(minValue!!) <= 0
            ) {
                minRow = i
                minValue = value
            }
        }
        var hasNegativeCol = false
        for (i in freeList) {
            val value: Double? = get(minRow, i)
            if (value!!.compareTo(0.0) < 0)
                hasNegativeCol = true
        }
        return !hasNegativeCol
    }

    private fun hasNegativeB(): Boolean = basisList.any { get(it, 0)!!.compareTo(0.0) < 0 }

    private fun add(row: Int, col: Int, value: Double) {
        if (!table.containsKey(row))
            table[row] = HashMap()
        val hashRow: MutableMap<Int, Double> = table[row]!!
        hashRow[col] = value
    }

    private fun calculateTarget(oneMatrix: MutableList<MutableList<Double>>, swappedColumns: List<Int>): MutableList<Double> {
        val targetFunction: MutableList<Double> = target
        val resultFunction: MutableList<Double> = mutableListOf()
        val row: MutableList<Double> = mutableListOf()
        for (i in 0 until targetFunction.size) {
            resultFunction.add(0.0)
            row.add(0.0)
        }
        for (k in oneMatrix.indices) {
            var swapIdx = 0
            row[0] = oneMatrix[k][0]
            for (i in 1 until oneMatrix[k].size) {
                if (i > basisList.size)
                    row[swappedColumns[swapIdx]] = oneMatrix[k][i] * -1
                else
                    row[i] = 0.0
                swapIdx++
            }
            swapIdx = k
            val value: Double = targetFunction[swappedColumns[swapIdx]]
            for (j in row.indices)
                row[j] = row[j] * value
            for (j in resultFunction.indices)
                resultFunction[j] = resultFunction[j] + row[j]
        }
        for (i in 1 until resultFunction.size)
            resultFunction[i] = resultFunction[i] + (
                if (basisList.contains(i)) 0.0 else targetFunction[i]
                )
        resultFunction[0] = resultFunction[0] + targetFunction[0]
        return resultFunction
    }

    fun findPivots(): List<Pair<Int, Int>> {
        val list: MutableList<Pair<Int, Int>> = ArrayList()
        val cols: MutableList<Int> = ArrayList()
        val targetFunc: Map<Int, Double> = table[0]!!
        targetFunc.forEach {
            if (it.value.compareTo(0.0) < 0 && it.key != 0)
                cols.add(it.key)
        }
        for (col in cols) {
            var minRel: Double? = null
            for (row in basisList) {
                val value: Double = table[row]!![col]!!
                if (value.compareTo(0.0) <= 0) {
                    continue
                }
                val rel: Double = table[row]!![0]!! / (value)
                if (minRel == null || rel.compareTo(minRel) <= 0)
                    minRel = rel
            }
            for (row in basisList) {
                if (!table[row]!![col]!!.equals(0.0)) {
                    val rel: Double = table[row]!![0]!! / table[row]!![col]!!
                    if (rel.equals(minRel))
                        list.add(Pair(row, col))
                }
            }
        }
        list.removeIf { it.second == 0 || it.first == 0 }
        if (hasNegativeB()) {
            var minRow = basisList[0]
            var minValue: Double? = get(basisList[0], 0)
            for (i in basisList) {
                val value: Double? = get(i, 0)
                if (
                    value!!.compareTo(0.0) < 0 &&
                    value.compareTo(minValue!!) <= 0
                ) {
                    minRow = i
                    minValue = value
                }
            }
            var hasNegativeCol = false
            var negativeCol = 0
            val minCol: Double? = get(minRow, freeList[0])
            for (i in freeList) {
                val value: Double? = get(minRow, i)
                if (value!!.compareTo(0.0) < 0 && value.compareTo(minCol!!) <= 0) {
                    hasNegativeCol = true
                    negativeCol = i
                }
            }
            if (hasNegativeCol) {
                list.clear()
                list.add(
                    Pair(minRow, negativeCol)
                )
            } else {
                list.clear()
                noSolution = true
                setAnswer()
            }
        }
        return list
    }

    fun generate(selectedPivot: Pair<Int, Int>): SimplexSolver {
        val generated = SimplexSolver(iteration + 1, selectedPivot, this)
        generated.basisList = basisList
        generated.freeList = freeList
        generated.target = target
        return generated
    }

    fun generate(): SimplexSolver {
        val generated = SimplexSolver()
        generated.prev = this
        generated.iteration = -1
        return generated
    }

    fun iterate() {
        if (isSolved) {
            setAnswer()
            return
        }
        swapRowColPivot()
        setPivotCell()
        setPivotRow()
        setPivotCol()
        setRemain()
        if (checkSolved()) {
            setAnswer()
            setSolved()
        }
    }

    private fun swapRowColPivot() {
        val row: Int = pivot.first
        val col: Int = pivot.second
        val basisList: MutableList<Int> = basisList
        val freeList: MutableList<Int> = freeList
        basisList.removeIf { it == row || it == 0 }
        freeList.removeIf { it == col || it == 0 }
        basisList.add(col)
        freeList.add(row)
        this.basisList = basisList
        this.freeList = freeList
    }

    private fun setRemain() {
        for (row in table.keys) {
            for (col in table[pivot.second]!!.keys) {
                if (row != pivot.second && col != pivot.first) {
                    val value: Double = prev!![row, col]!! - (
                        get(pivot.second, col)!! * prev!![row, pivot.second]!!
                        )
                    add(row, col, value)
                }
            }
        }
    }

    private fun setPivotCol() {
        val newPivotValue: Double? = get(pivot.second, pivot.first)
        for (row in prev!!.table.keys) {
            if (row == pivot.first) {
                continue
            }
            val value: Double = newPivotValue!! * prev!![row, pivot.second]!! * -1
            add(row, pivot.first, value)
        }
    }

    private fun setPivotRow() {
        val newPivotValue: Double? = get(pivot.second, pivot.first)
        for (col in prev!!.table[pivot.first]!!.keys) {
            if (col == pivot.second) {
                continue
            }
            val value: Double = newPivotValue!! * prev!![pivot.first, col]!!

            add(pivot.second, col, value)
        }
    }

    private fun setPivotCell() {
        val pivotValue: Double = prev!![pivot.first, pivot.second]!!
        add(pivot.second, pivot.first, 1 / pivotValue)
    }

    private fun setAnswer() {
        if (noSolution) {
            answer = null
            return
        }
        answer = MutableList(basisList.size + freeList.size + 1) { 0.0 }
        for (i in 1 until answer!!.size)
            answer!![i] = table.getOrDefault(i, mapOf(Pair(0, 0.0)))[0]!!
        answer!![0] = getTargetValue(answer!!)
    }

    private fun getTargetValue(answer: MutableList<Double>): Double {
        val target: MutableList<Double> = target
        var value = 0.0
        for (i in 1 until target.size)
            value += target[i] * answer[i]
        value += target[0]
        return value
    }

    private fun setSolved() {
        isSolved = true
    }

    private fun setPivot(pivot: Pair<Int, Int>) {
        this.pivot = pivot
    }

    private fun incIteration() {
        iteration++
    }
}
