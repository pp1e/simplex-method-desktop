package ru.uniyar.ac.components.simplex

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.badoo.reaktive.base.Consumer
import ru.uniyar.ac.calculations.SimplexSolver
import ru.uniyar.ac.components.simplex.SimplexStore.Intent
import ru.uniyar.ac.components.simplex.SimplexStore.State
import ru.uniyar.ac.entities.FractionType
import ru.uniyar.ac.entities.TaskType

internal class SimplexStoreProvider(
    private val storeFactory: StoreFactory,
    private val output: Consumer<SimplexComponent.Output>,
    private val solver: SimplexSolver,
    private val fractionType: FractionType,
    private val taskType: TaskType
) {

    fun provide(): SimplexStore =
        object :
            SimplexStore,
            Store<Intent, State, Nothing> by storeFactory.create(
                name = "SimplexStore${solver.iteration}",
                initialState = State(),
                bootstrapper = SimpleBootstrapper(Unit),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed class Msg {
        data class SimplexTableUpdated(val simplexTable: List<List<Double>>) : Msg()
        data class SolverLoaded(val solver: SimplexSolver) : Msg()
        data class FractionTypeChanged(val fractionType: FractionType) : Msg()
        data class PivotSelected(val selectedPivot: Pair<Int, Int>) : Msg()
        data class TaskTypeChanged(val taskType: TaskType) : Msg()
    }

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Unit, State, Msg, Nothing>() {
        override fun executeAction(action: Unit, getState: () -> State) {
            dispatch(Msg.SolverLoaded(solver))
            dispatch(Msg.TaskTypeChanged(taskType))
            // println(solver.table)
            dispatch(Msg.FractionTypeChanged(fractionType))
            // println(getState().solver.table.table)
        }

        override fun executeIntent(intent: Intent, getState: () -> State): Unit =
            when (intent) {
                is Intent.UpdateSimplexTable -> updateSimplexTable(intent.simplexTable)
                is Intent.SelectPivot -> selectPivot(intent.selectedPivot)
                is Intent.ChangeFractionType -> changeFractionType(intent.fractionType)
            }

        private fun changeFractionType(fractionType: FractionType) {
            dispatch(Msg.FractionTypeChanged(fractionType))
        }

        private fun selectPivot(selectedPivot: Pair<Int, Int>) {
            dispatch(Msg.PivotSelected(selectedPivot))
        }

        private fun updateSimplexTable(simplexTable: List<List<Double>>) {
            dispatch(Msg.SimplexTableUpdated(simplexTable))
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.SimplexTableUpdated -> copy(simplexTable = msg.simplexTable)
                is Msg.SolverLoaded -> copy(solver = msg.solver)
                is Msg.FractionTypeChanged -> copy(fractionType = msg.fractionType)
                is Msg.PivotSelected -> copy(selectedPivot = msg.selectedPivot,)
                is Msg.TaskTypeChanged -> copy(taskType = msg.taskType)
            }
    }
}
