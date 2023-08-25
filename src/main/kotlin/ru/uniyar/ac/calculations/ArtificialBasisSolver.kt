package ru.uniyar.ac.calculations

import com.google.gson.Gson
import ru.uniyar.ac.entities.SolutionMethod

class ArtificialBasisSolver {
    var target: MutableList<Double> = mutableListOf()
    var restrict: MutableList<MutableList<Double>> = mutableListOf()
    var varCount = 0
    var restrictCount = 0
    var simplexSolver: SimplexSolver
    var isArtificialSolved = false
    var isSolved = false
    private var artificialAnswer: MutableList<Double>? = null
    var noSolution = false
    var mode = SolutionMethod.ARTIFICIAL_BASIS
    var iteration: Int
        get() = simplexSolver.iteration
        set(iteration) { simplexSolver.iteration = iteration }

    val basisList: List<Int>
        get() = simplexSolver.basisList

    val freeList: List<Int>
        get() = simplexSolver.freeList

    val answer: MutableList<Double>?
        get() {
            if (noSolution)
                return null
            return if (isArtificialSolved && !isSolved)
                artificialAnswer
            else
                simplexSolver.answer
        }

    private val isArtificialHasNoSolution: Boolean
        get() {
            if (isOptimalSolution) {
                val isAllExtraZero = simplexSolver.basisList
                    .filter { it > varCount }
                    .all { get(it, 0).equals(0.0) }

                if (isAllExtraZero)
                    return false

                val isAnyExtraNotZero = simplexSolver.basisList
                    .filter { it > varCount }
                    .any { !get(it, 0).equals(0.0) }

                return isAnyExtraNotZero
            } else
                return false
        }

    private val isOptimalSolution: Boolean
        get() {
            val deltas: List<Double> = calcDeltas()
            return deltas.none { it.compareTo(0.0) > 0 }
        }

    fun init(
        target: MutableList<Double>,
        restrict: MutableList<MutableList<Double>>,
        varCount: Int,
        restrictCount: Int,
        basisList: List<Int>
    ) {
        this.target = target
        this.restrict = restrict
        this.varCount = varCount
        this.restrictCount = restrictCount

        if (isArtificialSolved) {
            simplexSolver.init(
                target = target,
                restrict = restrict,
                varCount = varCount,
                restrictCount = restrictCount,
                basisList = basisList
            )
            if (checkSolved())
                isSolved = true
        } else {
            initConvertedConditions()
            if (checkArtificialSolved())
                isArtificialSolved = true
        }
    }

    private fun initConvertedConditions() {
        val eTarget: MutableList<Double> = convertTarget()
        val eVarCount = varCount + restrictCount
        val eRestrictCount = restrictCount
        val eBasisList = convertBasisList()
        val eRestrict: MutableList<MutableList<Double>> = convertRestrict()
        simplexSolver.init(
            target = eTarget,
            restrict = eRestrict,
            varCount = eVarCount,
            restrictCount = eRestrictCount,
            basisList = eBasisList
        )
    }

    private fun convertTarget(): MutableList<Double> {
        val eTarget: MutableList<Double> = mutableListOf()
        for (i in 0 until (varCount + restrictCount + 1))
            eTarget.add(if (i >= varCount + 1) 1.0 else 0.0)

        return eTarget
    }

    private fun convertBasisList(): List<Int> {
        val list: MutableList<Int> = mutableListOf()
        var newVar = varCount + 1
        for (i in 0 until restrictCount)
            list.add(newVar++)
        return list
    }

    private fun convertRestrict(): MutableList<MutableList<Double>> {
        var newVar = varCount + 1
        val eRestrict: MutableList<MutableList<Double>> = mutableListOf()

        for (i in 0 until restrictCount) {
            eRestrict.add(mutableListOf())

            for (j in 0 until (varCount + restrictCount + 1)) {
                if (j == newVar)
                    eRestrict.last().add(1.0)
                else if (j > varCount)
                    eRestrict.last().add(0.0)
                else
                    eRestrict.last().add(restrict[i][j])

                if (restrict.last().first().compareTo(0.0) < 0 && j <= varCount)
                    eRestrict[i][j] = restrict[i][j] * -1
            }
            newVar++
        }
        return eRestrict
    }

    operator fun get(basis: Int, free: Int): Double {
        return simplexSolver[basis, free]!!
    }

    fun setTable(table: MutableMap<Int, MutableMap<Int, Double>>) {
        this.simplexSolver.table = table
    }

    fun findPivots(): List<Pair<Int, Int>> {
        return simplexSolver.findPivots()
    }

    private fun findForEmptyPivots(): List<Pair<Int, Int>> {
        val list: MutableList<Pair<Int, Int>> = mutableListOf()
        for (col in simplexSolver.freeList)
            for (row in simplexSolver.basisList) {
                val value: Double = simplexSolver[row, col]!!
                if (value.compareTo(0.0) != 0 && col > varCount)
                    continue
                // val rel: Double = table[row, 0]!! / value
                list.add(Pair(row, col))
            }
        list.removeIf { it.second == 0 || it.first == 0 }
        return list
    }

    private fun calcDeltas(): MutableList<Double> {
        val deltas: MutableList<Double> = mutableListOf()

        for (i in 0 until (freeList.size + 1))
            deltas.add(0.0)
        val target: MutableList<Double> = target
        var idXCol = 0
        for (col in this.freeList) {
            var delta = 0.0
            for (row in basisList) {
                var c: Double
                if (row > varCount)
                    c = 1000000.0
                else
                    c = if (row < target.size) target[row] else 0.0
                val value = get(row, col)
                delta += c * value
            }
            var c: Double
            if (col > varCount)
                c = 1000000.0
            else
                c = if (col < target.size) target[col] else 0.0
            delta -= c
            deltas[idXCol++] = delta
        }
        return deltas
    }

    fun generate(selectedPivot: Pair<Int, Int>): ArtificialBasisSolver {
        val newSolver = this.clone()
        newSolver.simplexSolver = newSolver.simplexSolver.generate(selectedPivot)
        return newSolver
    }

    fun iterate() {
        simplexSolver.iterate()
        if (checkArtificialSolved() && mode == SolutionMethod.ARTIFICIAL_BASIS) {
            setArtificialAnswer()
            isArtificialSolved = true
            return
        }
        if (mode == SolutionMethod.ARTIFICIAL_BASIS)
            isSolved = false
        if (mode == SolutionMethod.SIMPLEX)
            if (checkSolved())
                isSolved = true
    }

    private fun setArtificialAnswer() {
        if (noSolution) {
            artificialAnswer = null
            return
        }
        artificialAnswer = mutableListOf()
        artificialAnswer!!.add(simplexSolver[0, 0]!! * -1)
        for (i in 1 until (varCount + restrictCount + 1))
            artificialAnswer!!.add(simplexSolver.table.getOrDefault(i, mapOf(Pair(0, 0.0)))[0]!!)
    }

    init {
        simplexSolver = SimplexSolver()
    }

    fun checkArtificialSolved(): Boolean {
        // val countExtraVar = basisList.filter{ it > varCount }.count()
        val hasSolution = simplexSolver.isSolved
        if (hasSolution) {
            artificialAnswer = answer
            if (artificialAnswer == null || artificialAnswer!![0].compareTo(0.0) > 0)
                noSolution = true
        }
        if (isArtificialHasNoSolution)
            noSolution = true
        return hasSolution || noSolution
    }

    fun checkSolved(): Boolean {
        isSolved = simplexSolver.isSolved
        return isSolved
    }

    fun clone(): ArtificialBasisSolver {
        val newTable = Gson().toJson(this, ArtificialBasisSolver::class.java)
        return Gson().fromJson(newTable, ArtificialBasisSolver::class.java)
    }
}
