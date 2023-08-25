package ru.uniyar.ac.components.artificial

import com.arkivanov.mvikotlin.core.store.Store
import ru.uniyar.ac.calculations.ArtificialBasisSolver
import ru.uniyar.ac.entities.FractionType
import ru.uniyar.ac.entities.TaskType

internal interface ArtificialBasisStore : Store<ArtificialBasisStore.Intent, ArtificialBasisStore.State, Nothing> {
    sealed class Intent {
        data class UpdateSimplexTable(val simplexTable: List<List<Double>>) : Intent()
        data class SelectPivot(val selectedPivot: Pair<Int, Int>) : Intent()
        data class ChangeFractionType(val fractionType: FractionType) : Intent()
    }

    data class State(
        val simplexTable: List<List<Double>> = emptyList(),
        val fractionType: FractionType = FractionType.DECIMAL,
        val solver: ArtificialBasisSolver = ArtificialBasisSolver(),
        val selectedPivot: Pair<Int, Int> = Pair(-1, -1),
        val taskType: TaskType = TaskType.MIN
    )
}
