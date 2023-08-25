package ru.uniyar.ac.components.main

import com.arkivanov.mvikotlin.core.store.Store
import ru.uniyar.ac.entities.FractionType
import ru.uniyar.ac.entities.SolutionMethod
import ru.uniyar.ac.entities.TaskType

internal interface MainStore : Store<MainStore.Intent, MainStore.State, Nothing> {
    sealed class Intent {
        data class ChangeRestrictionCount(val count: Int) : Intent()
        data class ChangeVariablesCount(val count: Int) : Intent()
        data class ChangeFractionType(val fractionType: FractionType) : Intent()
        data class ChangeSolutionMethod(val solutionMethod: SolutionMethod) : Intent()
        data class ChangeTaskType(val taskType: TaskType) : Intent()
        data class ChangeRestrictionsElement(val row: Int, val column: Int, val value: Pair<Boolean, String>) : Intent()
        data class ChangeFunctionElement(val column: Int, val value: Pair<Boolean, String>) : Intent()
        data class LoadRestrictions(val restrictions: List<List<Pair<Boolean, String>>>) : Intent()
        data class LoadFunction(val function: List<Pair<Boolean, String>>) : Intent()
        object ToggleFileChooseWindow : Intent()
    }

    data class State(
        val restrictions: List<List<Pair<Boolean, String>>> = listOf(
            listOf(Pair(false, "0"), Pair(false, "0"), Pair(false, "0")),
            listOf(Pair(false, "0"), Pair(false, "0"), Pair(false, "0"))
        ),
        val function: List<Pair<Boolean, String>> = listOf(Pair(false, "0"), Pair(false, "0"), Pair(false, "0")),
        val fractionType: FractionType = FractionType.COMMON,
        val solutionMethod: SolutionMethod = SolutionMethod.SIMPLEX,
        val taskType: TaskType = TaskType.MIN,
        val fileChooseWindowOpened: Boolean = false
    )
}
