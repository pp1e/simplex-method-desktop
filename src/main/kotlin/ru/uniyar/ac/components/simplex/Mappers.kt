package ru.uniyar.ac.components.simplex

import ru.uniyar.ac.entities.PivotData
import ru.uniyar.ac.utils.columnNamesFromSimplexTable
import ru.uniyar.ac.utils.elementsFromSimplexTable
import ru.uniyar.ac.utils.inverseFirstIfMax
import ru.uniyar.ac.utils.rowNamesFromSimplexTable
import ru.uniyar.ac.utils.toString

internal val stateToModel: (SimplexStore.State) -> SimplexComponent.Model =
    { state ->
        SimplexComponent.Model(
            simplexTable = elementsFromSimplexTable(state.solver.table, state.solver.findPivots(), state.fractionType)
                .map {
                    it.map {
                        if ((it.second.row == state.selectedPivot.first) and (it.second.column == state.selectedPivot.second))
                            Pair(it.first, PivotData(it.second.row, it.second.column, true))
                        else it
                    }
                },
            columnNames = columnNamesFromSimplexTable(state.solver.table),
            rowNames = rowNamesFromSimplexTable(state.solver.table),
            artificialAnswer = (state.solver.answer ?: mutableListOf()).inverseFirstIfMax(state.taskType).map { it.toString(state.fractionType) },
            solved = state.solver.isSolved,
            iteration = state.solver.iteration,
            fractionType = state.fractionType,
            noSolution = state.solver.noSolution
        )
    }
