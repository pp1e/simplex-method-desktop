package ru.uniyar.ac.components.main

internal val stateToModel: (MainStore.State) -> MainComponent.Model =
    { state ->
        MainComponent.Model(
            restrictions = state.restrictions,
            function = state.function,
            restrictionCount = state.restrictions.size,
            variablesCount = state.function.size - 1,
            fractionType = state.fractionType,
            taskType = state.taskType,
            solutionMethod = state.solutionMethod,
            isDataCorrect = !(state.restrictions.any { it.any { it.first } } || state.function.any { it.first })
        )
    }
