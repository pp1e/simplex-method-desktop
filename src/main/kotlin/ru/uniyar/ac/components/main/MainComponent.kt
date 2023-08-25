package ru.uniyar.ac.components.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.base.invoke
import ru.uniyar.ac.calculations.ArtificialBasisSolver
import ru.uniyar.ac.calculations.SimplexSolver
import ru.uniyar.ac.entities.FractionType
import ru.uniyar.ac.entities.SolutionMethod
import ru.uniyar.ac.entities.TaskType
import ru.uniyar.ac.storage.readFromFile
import ru.uniyar.ac.storage.saveInFile
import ru.uniyar.ac.utils.asValue
import ru.uniyar.ac.utils.parseFraction
import java.lang.ArithmeticException
import java.util.*

class MainComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    private val output: Consumer<Output>,
) : ComponentContext by componentContext {

    private val store =
        instanceKeeper.getStore {
            MainStoreProvider(
                storeFactory = storeFactory,
            ).provide()
        }

    val models: Value<Model> = store.asValue().map(stateToModel)

    fun onRestrictionCountChanged(count: String) {
        try {
            store.accept(MainStore.Intent.ChangeRestrictionCount(count.toInt()))
        } catch (_: NumberFormatException) { println(count) }
    }

    fun onVariablesCountChanged(count: String) {
        try {
            store.accept(MainStore.Intent.ChangeVariablesCount(count.toInt()))
        } catch (_: NumberFormatException) { println(count) }
    }

    fun onFractionTypeChanged(fractionType: FractionType) {
        store.accept(MainStore.Intent.ChangeFractionType(fractionType))
    }

    fun onSolutionMethodChanged(solutionMethod: SolutionMethod) {
        store.accept(MainStore.Intent.ChangeSolutionMethod(solutionMethod))
    }

    fun onTaskTypeChanged(taskType: TaskType) {
        store.accept(MainStore.Intent.ChangeTaskType(taskType))
    }

    fun onRestrictionElementChanged(row: Int, column: Int, value: String) {
        var isError = false

        try {
            if (value.contains("/")) {
                val fractionParts = value.split("/")
                fractionParts[0].toInt()/fractionParts[1].toInt()
            } else
                value.toDouble()
        } catch (_: NumberFormatException) {
            isError = true
        } catch (_: IndexOutOfBoundsException) {
            isError = true
        } catch (_: ArithmeticException) {
            isError = true
        }

        store.accept(MainStore.Intent.ChangeRestrictionsElement(row, column, Pair(isError, value)))
    }

    fun onFunctionElementChanged(column: Int, value: String) {
        var isError = false

        try {
            if (value.contains("/")) {
                val fractionParts = value.split("/")
                fractionParts[0].toInt()/fractionParts[1].toInt()
            } else
                value.toDouble()
        } catch (_: NumberFormatException) {
            isError = true
        } catch (_: IndexOutOfBoundsException) {
            isError = true
        } catch (_: ArithmeticException) {
            isError = true
        }
        store.accept(MainStore.Intent.ChangeFunctionElement(column, Pair(isError, value)))
    }

    fun onContinueClicked() {
        val restrictions = store.state.restrictions.map { it.toMutableList() }
        restrictions.forEach { Collections.rotate(it, 1) }

        val function = store.state.function.toMutableList()
        Collections.rotate(function, 1)

        when (store.state.solutionMethod) {
            SolutionMethod.SIMPLEX -> {
                val solver = SimplexSolver()
                val target = function.map { it.second.parseFraction() }
                val restrict = restrictions.map { it.map { it.second.parseFraction() } }

                solver.init(
                    target = if (store.state.taskType == TaskType.MAX) target.map { it * -1 }.toMutableList() else target.toMutableList(),
                    restrict = restrict.map { it.toMutableList() }.toMutableList(),
                    varCount = target.size - 1,
                    restrictCount = restrictions.size,
                    basisList = List(restrictions.size) { it + 1 }
                )

                output(Output.SimplexTransit(solver, store.state.fractionType, store.state.taskType))
            }
            SolutionMethod.GRAPHIC -> output(Output.GraphicTransit(store.state.fractionType))
            SolutionMethod.ARTIFICIAL_BASIS -> {
                val solver = ArtificialBasisSolver()
                val target = function.map { it.second.parseFraction() }
                val restrict = restrictions.map { it.map { it.second.parseFraction() } }

                solver.init(
                    target = if (store.state.taskType == TaskType.MAX) target.map { it * -1 }.toMutableList() else target.toMutableList(),
                    restrict = restrict.map { it.toMutableList() }.toMutableList(),
                    varCount = target.size - 1,
                    restrictCount = restrictions.size,
                    basisList = List(restrictions.size) { it + 1 }
                )

                output(Output.ArtificialBasisTransit(solver, store.state.fractionType, store.state.taskType))
            }
        }
    }

    fun saveSimplexData(file: String) {
        saveInFile(store.state.restrictions, store.state.function, file)
    }

    fun loadSimplexData(file: String) {
        try {
            val data = readFromFile(file)
            store.accept(MainStore.Intent.LoadRestrictions(data.restriction))
            store.accept(MainStore.Intent.LoadFunction(data.function))
        } catch (_: Exception) {
            print("Ошибка загрузки из файла!")
        }
    }

    fun fileChooseWindowToggled() {
        store.accept(MainStore.Intent.ToggleFileChooseWindow)
    }

    data class Model(
        val restrictions: List<List<Pair<Boolean, String>>>,
        val function: List<Pair<Boolean, String>>,
        val restrictionCount: Int,
        val variablesCount: Int,
        val fractionType: FractionType,
        val taskType: TaskType,
        val solutionMethod: SolutionMethod,
        val isDataCorrect: Boolean
    )

    sealed class Output {
        data class SimplexTransit(
            val solver: SimplexSolver,
            val fractionType: FractionType,
            val taskType: TaskType
        ) : Output()
        data class GraphicTransit(val fractionType: FractionType) : Output()
        data class ArtificialBasisTransit(
            val solver: ArtificialBasisSolver,
            val fractionType: FractionType,
            val taskType: TaskType
        ) : Output()
    }
}
