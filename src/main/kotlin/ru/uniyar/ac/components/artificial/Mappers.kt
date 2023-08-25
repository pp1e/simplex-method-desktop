package ru.uniyar.ac.components.artificial

import ru.uniyar.ac.entities.PivotData
import ru.uniyar.ac.utils.columnNamesFromSimplexTable
import ru.uniyar.ac.utils.elementsFromSimplexTable
import ru.uniyar.ac.utils.inverseFirstIfMax
import ru.uniyar.ac.utils.rowNamesFromSimplexTable
import ru.uniyar.ac.utils.toString

internal val stateToModel: (ArtificialBasisStore.State) -> ArtificialBasisComponent.Model =
    { state ->
        ArtificialBasisComponent.Model(
            simplexTable = elementsFromSimplexTable(state.solver.simplexSolver.table, state.solver.findPivots(), state.fractionType)
                .map {
                    it.map {
                        if ((it.second.row == state.selectedPivot.first) and (it.second.column == state.selectedPivot.second))
                            Pair(it.first, PivotData(it.second.row, it.second.column, true))
                        else Pair(it.first, PivotData(it.second.row, it.second.column))
                    }
                },
            columnNames = columnNamesFromSimplexTable(state.solver.simplexSolver.table),
            rowNames = rowNamesFromSimplexTable(state.solver.simplexSolver.table),
            artificialAnswer = (state.solver.answer ?: mutableListOf()).inverseFirstIfMax(state.taskType).map { it.toString(state.fractionType) },
            iteration = state.solver.iteration,
            fractionType = state.fractionType,
            noSolution = state.solver.noSolution
        )
    }
